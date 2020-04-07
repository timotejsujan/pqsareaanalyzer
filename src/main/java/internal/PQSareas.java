package internal;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

class Scaffold {
    Scaffold(String name) {
        this.name = name;
    }
    String name;
    String sequence = "";
    List<QuadruplexPositive> QPsPositive= new ArrayList<>();
    List<QuadruplexNegative> QPsNegative= new ArrayList<>();
}

class QuadruplexPositive {
    Integer position;
    Integer width;
}

class QuadruplexNegative {
    Integer position;
    Integer width;
}

class Genom {
    Genom(String name) {
        this.name = name;
    }
    String name;
    List<Scaffold> scaffolds = new ArrayList<>();
}

public class PQSareas {

    public PQSareas(TextArea outputArea) {
        this.printStream = new PrintStream(new CustomOutputStream(outputArea));
    }
    private int AREA = 30;

    private String input1Path = "";
    private String input2Path = "";
    private String outputPath = "";
    private String outputName = "";
    private String outputNamePQS = "";
    public PrintStream printStream;

    private boolean whole = true;

    private void printStatus(String status) {
        Platform.runLater(() -> printStream.println(status));
    }

    public void setAREA(int AREA) {
        this.AREA = AREA;
    }

    public void setInput1Path(String inputPath) {
        this.input1Path = inputPath;
    }

    public void setInput2Path(String inputPath) {
        this.input2Path = inputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public void setOutputNamePQS(String outputName) {
        this.outputName = outputName + ".txt";
        this.outputNamePQS = outputName + "_pqs.txt";
    }

    public void start() throws IOException {
        List<File> files = new ArrayList<>();
        files.add(new File(input1Path));

        List<Genom> sequences = new ArrayList<>();
        sequences.add(getGenom(files.get(0)));

        List<File> g4s = new ArrayList<>();
        g4s.add(new File(input2Path));

        getQuadruplexes(g4s.get(0), sequences.get(0));

        for (Genom g : sequences) {
            try {
                Files.write(Paths.get(outputPath+"/"+outputName), (";"+g.name + "\n").getBytes());
                Files.write(Paths.get(outputPath+"/"+outputNamePQS), (";"+g.name + "\n").getBytes());

                for (Scaffold s : g.scaffolds) {
                    if (Thread.interrupted()) return;
                    if (whole) {
                        printPositiveWhole(g, s);
                        printNegativeWhole(g, s);
                    } else {
                        printPositive(g, s);
                        printNegative(g, s);
                    }
                }
            }catch (IOException ignored) {
                printStatus("POZOR NEZAPSALO SE" + ignored.toString());
            }
        }
        if (Thread.interrupted()) return;
        printStatus("the process has ended");
    }

    private static void getQuadruplexes(File file, Genom g) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        int counter = -1;
        st = br.readLine();
        while ((st = br.readLine()) != null) {
            if (Thread.interrupted()) return;
            if (!st.isEmpty() && st.charAt(0) == '>') {
                counter++;
                continue;
            } else if (counter == -1){
                continue;
            }
            if ("+".equals(st.split(",")[2])) {
                QuadruplexPositive newQP = new QuadruplexPositive();
                newQP.position = Integer.parseInt(st.split(",")[0]) - 1;
                newQP.width = Integer.valueOf(st.split(",")[1]);
                g.scaffolds.get(counter).QPsPositive.add(newQP);
            } else {
                QuadruplexNegative newQP = new QuadruplexNegative();
                newQP.position = Integer.parseInt(st.split(",")[0]) - 1;
                newQP.width = Integer.valueOf(st.split(",")[1]);
                g.scaffolds.get(counter).QPsNegative.add(newQP);
            }
        }
    }

    private static Genom getGenom (File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        Genom gen = new Genom(file.getName());
        String st;
        Scaffold s = null;
        StringBuilder str = new StringBuilder();
        int scaffoldNumber = 0;
        while ((st = br.readLine()) != null) {
            if (!st.isEmpty() && st.charAt(0) == '>') {

                if (s != null) {
                    s.sequence = str.toString().toUpperCase();
                }
                str = new StringBuilder();
                s = new Scaffold(Integer.toString(scaffoldNumber));
                gen.scaffolds.add(s);
                scaffoldNumber++;
                continue;
            }
            str.append(st);
        }
        assert s != null;
        s.sequence = str.toString().toUpperCase();

        return gen;
    }

    private static StringBuilder reverseSequence (StringBuilder s) {
        s.reverse();
        StringBuilder n = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case 'A': n.append('T'); break;
                case 'T': n.append('A'); break;
                case 'C': n.append('G'); break;
                case 'G': n.append('C'); break;
                //case 'Q': n.append("Q"); break;
                default: n.append('N'); break;
            }
        }
        return n;
    }

    private void printPositive(Genom g, Scaffold s) throws IOException {
        int QPnumber = 0;
        StringBuilder line = null;
        QuadruplexPositive nextQP;
        Boolean leftSideAlreadyWritten = null;
        for (QuadruplexPositive q : s.QPsPositive) {
            if (Thread.interrupted()) return;

            nextQP = (QPnumber != s.QPsPositive.size() - 1 ? s.QPsPositive.get(QPnumber+1) : null);
            int j = Math.max(q.position - AREA, 0);
            int maxJ = Math.min(q.position + AREA + q.width, s.sequence.length());

            if (leftSideAlreadyWritten != null && !leftSideAlreadyWritten) {
                line = new StringBuilder();
                line.append("\n>+L-").append(s.name).append("-").append(QPnumber).append("\n");
                String newS = s.sequence.substring(j, q.position);
                line.append(newS.replace("N", ""));
                Files.write(Paths.get(outputPath+"/"+outputName), line.toString().getBytes(), StandardOpenOption.APPEND);
            }

            if (nextQP != null && nextQP.position - q.position <= 2 * AREA) {
                maxJ = nextQP.position;
                leftSideAlreadyWritten = true;
            } else {
                leftSideAlreadyWritten = false;
            }

            if (maxJ > q.position + q.width) {
                line = new StringBuilder();
                line.append("\n>+R-").append(s.name).append("-").append(QPnumber).append("\n");
                String newS = s.sequence.substring(q.position + q.width, maxJ);
                line.append(newS.replace("N", ""));
                Files.write(Paths.get(outputPath+"/"+outputName), line.toString().getBytes(), StandardOpenOption.APPEND);
            }
            QPnumber++;


        }
    }

    // todo tiskne se poslední cast?
    private void printPositiveWhole(Genom g, Scaffold s) throws IOException {
        int QPnumber = 0;
        for (QuadruplexPositive q : s.QPsPositive) {
            if (Thread.interrupted()) return;

            int j = q.position - AREA;
            int maxJ = q.position + q.width + AREA;
            if (j < 0 || maxJ > s.sequence.length()) {
                QPnumber++;
                continue;
            }

            StringBuilder leftLine = new StringBuilder();
            leftLine.append("\n>+L-").append(s.name).append("-").append(QPnumber).append("\n");
            leftLine.append(s.sequence, j, q.position);//.replace("N", ""));

            leftLine.append(s.sequence, q.position + q.width, maxJ);//.replace("N", ""));
            if (leftLine.toString().contains("N")) {
                QPnumber++;
                continue;
            }
            Files.write(Paths.get(outputPath+"/"+outputName), leftLine.toString().getBytes(), StandardOpenOption.APPEND);

            StringBuilder pqs = new StringBuilder();
            pqs.append("\n>+L-").append(s.name).append("-").append(QPnumber).append("\n");
            pqs.append(s.sequence, q.position, q.position + q.width);
            Files.write(Paths.get(outputPath+"/"+outputNamePQS), pqs.toString().getBytes(), StandardOpenOption.APPEND);

            QPnumber++;
        }
    }

    private void printNegative(Genom g, Scaffold s) throws IOException {
        int QPnumber = 0;
        StringBuilder line = null;
        QuadruplexNegative nextQP;
        Boolean leftSideAlreadyWritten = null;
        for (QuadruplexNegative q : s.QPsNegative) {
            if (Thread.interrupted()) return;
            nextQP = (QPnumber != s.QPsNegative.size() - 1 ? s.QPsNegative.get(QPnumber+1) : null);
            int j = Math.max(q.position - AREA, 0);
            int maxJ = Math.min(q.position + AREA + q.width, s.sequence.length());

            if (leftSideAlreadyWritten != null && !leftSideAlreadyWritten) {
                line = new StringBuilder();
                String newS = s.sequence.substring(j, q.position);
                line.append(newS.replace("N", ""));
                line = reverseSequence(line);
                line.insert(0, "\n>-L-"+s.name+"-"+QPnumber+"\n");
                Files.write(Paths.get(outputPath+"/"+outputName), line.toString().getBytes(), StandardOpenOption.APPEND);
            }

            if (nextQP != null && nextQP.position - q.position <= 2 * AREA) {
                maxJ = nextQP.position;
                leftSideAlreadyWritten = true;
            } else {
                leftSideAlreadyWritten = false;
            }

            if (maxJ > q.position + q.width) {
                line = new StringBuilder();
                String newS = s.sequence.substring(q.position + q.width, maxJ);
                line.append(newS.replace("N", ""));
                line = reverseSequence(line);
                line.insert(0, "\n>-R-"+s.name+"-"+QPnumber+"\n");
                Files.write(Paths.get(outputPath+"/"+outputName), line.toString().getBytes(), StandardOpenOption.APPEND);
            }
            QPnumber++;

        }
    }

    private void printNegativeWhole(Genom g, Scaffold s) throws IOException {
        int QPnumber = 0;
        StringBuilder line = null;
        for (QuadruplexNegative q : s.QPsNegative) {
            if (Thread.interrupted()) return;


            int j = q.position - AREA;
            int maxJ = q.position + q.width + AREA;
            if (j < 0 || maxJ > s.sequence.length()) {
                QPnumber++;
                continue;
            }

            StringBuilder leftLine = new StringBuilder();
            leftLine.append(s.sequence, j, q.position); //.replace("N", ""));
            leftLine = reverseSequence(leftLine);
            leftLine.insert(0, "\n>-L-"+s.name+"-"+QPnumber+"\n");

            line = new StringBuilder();
            line.append(s.sequence, q.position + q.width, maxJ);//.replace("N", ""));
            line = reverseSequence(line);

            leftLine.append(line);
            if (leftLine.toString().contains("N")) {
                QPnumber++;
                continue;
            }
            Files.write(Paths.get(outputPath + "/" + outputName), leftLine.toString().getBytes(), StandardOpenOption.APPEND);

            StringBuilder pqs = new StringBuilder();
            pqs.append(s.sequence, q.position, q.position + q.width);
            pqs = reverseSequence(pqs);
            pqs.insert(0, "\n>-L-"+s.name+"-"+QPnumber+"\n");
            Files.write(Paths.get(outputPath+"/"+outputNamePQS), pqs.toString().getBytes(), StandardOpenOption.APPEND);

            QPnumber++;

        }
    }
}
