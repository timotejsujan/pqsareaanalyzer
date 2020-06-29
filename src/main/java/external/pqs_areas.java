package external;

import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class pqs_areas extends base {

    public pqs_areas(TextArea outputArea) {
        super(outputArea);
    }
    private int area_size = 30;

    private String input2Path = "";
    private String outputNamePQS = "";

    public void set_area(int n) {
        area_size = n;
    }

    public void setInput2Path(String inputPath) {
        this.input2Path = inputPath;
    }

    public void setOutputNamePQS(String outputName) {
        output_name = outputName + ".txt";
        outputNamePQS = outputName + "_pqs.txt";
    }

    public void start() throws IOException {
        if (!is_base_valid()) return;

        File f = new File(input_path);

        genom g = get_genom(f);

        File g4 = new File(input2Path);

        get_pqs(g4, g);

        try {
            Files.write(Paths.get(output_path +"/"+ output_name), (";"+g.name + "\n").getBytes());
            Files.write(Paths.get(output_path +"/"+outputNamePQS), (";"+g.name + "\n").getBytes());

            for (scaffold s : g.scaffolds) {
                if (s.positive_g4.size() + s.negative_g4.size() != 0) {
                    //s.sequence = get_scaffold(f, s.number);
                    if (Thread.interrupted()) return;
                    print_positive_pqs(s);
                    print_negative_pqs(s);
                    s.sequence = "";
                }
            }
        }catch (IOException ignored) {
            print_status("POZOR NEZAPSALO SE" + ignored.toString());
        }

        if (Thread.interrupted()) return;
        print_status("the process has ended");
    }

    private void get_pqs(File file, genom g) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        int counter = -1;
        while (!Thread.interrupted() && (line = br.readLine()) != null) {
            if (line.startsWith(";")) {
                counter = Integer.parseInt(line.split(": ")[1]);
            } else if (counter != -1 && line.startsWith(">")){
                String pqs = br.readLine();

                String[] info = line.split(";");
                int start = Integer.parseInt(info[2].split("=")[1]);
                int end = Integer.parseInt(info[3].split("=")[1]);
                String strand = (info[4].split("=")[1]);

                pqs new_pqs = new pqs(start - 1, end, pqs);
                if ("+".equals(strand)) {
                    g.scaffolds.get(counter - 1).positive_g4.add(new_pqs);
                } else {
                    g.scaffolds.get(counter - 1).negative_g4.add(new_pqs);
                }
            }
        }
    }

    private String get_scaffold(File f, int n) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;
        StringBuilder sb = new StringBuilder();
        int scaff_number = -1;
        while (scaff_number != n){
            line = br.readLine();
            if (!line.isEmpty() && line.charAt(0) == '>') {
                scaff_number++;
            }
        }
        while ((line = br.readLine()) != null) {
            if (!line.isEmpty() && line.charAt(0) == '>') {
                break;
            }
            sb.append(line);
        }
        return sb.toString();
    }

    private genom get_genom(File f) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(f));
        genom gen = new genom(f.getName());
        String line;
        scaffold s = null;
        StringBuilder sequence = new StringBuilder();
        int scaff_number = 1;
        while ((line = br.readLine()) != null) {
            if (!line.isEmpty() && line.charAt(0) == '>') {
                if (scaff_number != 1) s.sequence = sequence.toString();
                s = new scaffold(scaff_number);
                gen.scaffolds.add(s);
                scaff_number++;
                sequence = new StringBuilder();
            } else{
                sequence.append(line);
            }
        }
        assert s != null;
        s.sequence = sequence.toString();
        return gen;
    }

    private StringBuilder get_reverse_sequence(StringBuilder s) {
        s.reverse();
        StringBuilder n = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case 'A': n.append('T'); break;
                case 'T': n.append('A'); break;
                case 'C': n.append('G'); break;
                case 'G': n.append('C'); break;
                default: n.append('N'); break;
            }
        }
        return n;
    }

    // todo tiskne se poslední cast?
    private void print_positive_pqs(scaffold s) throws IOException {
        int pqs_number = 0;
        for (pqs q : s.positive_g4) {
            if (Thread.interrupted()) return;

            int j = q.start - area_size;
            int maxJ = q.end + area_size;//q.start + q.end + area_size;
            if (j < 0 || maxJ > s.sequence.length()) {
                //if ( j < 0 ) j = 0;
                //if ( maxJ > s.sequence.length()) maxJ = s.sequence.length();
                pqs_number++;
                continue;
            }

            StringBuilder leftLine = new StringBuilder();
            leftLine.append("\n>+L-").append(s.number).append("-").append(pqs_number).append("\n");
            leftLine.append(s.sequence, j, q.start);//.replace("N", ""));

            leftLine.append(s.sequence, q.end /*q.start + q.end*/, maxJ);//.replace("N", ""));
            if (leftLine.toString().contains("N")) {
                pqs_number++;
                continue;
            }
            Files.write(Paths.get(output_path +"/"+ output_name), leftLine.toString().getBytes(), StandardOpenOption.APPEND);

            String pqs = "\n>+L-" + s.number + "-" + pqs_number + "\n" + q.sequence;
                    //s.sequence.substring(q.start, q.end /*q.start + q.end*/);
            Files.write(Paths.get(output_path +"/"+outputNamePQS), pqs.getBytes(), StandardOpenOption.APPEND);

            pqs_number++;
        }
    }

    private void print_negative_pqs(scaffold s) throws IOException {
        int pqs_number = 0;
        StringBuilder right_area = null;
        for (pqs q : s.negative_g4) {
            if (Thread.interrupted()) return;


            int j = q.start - area_size;
            int maxJ = q.end + area_size; //q.start + q.end + area_size;
            if (j < 0 || maxJ > s.sequence.length()) {
                //if ( j < 0 ) j = 0;
                //if ( maxJ > s.sequence.length()) maxJ = s.sequence.length();
                pqs_number++;
                continue;
            }

            StringBuilder area = new StringBuilder();
            area.append(s.sequence, j, q.start); //.replace("N", ""));
            area = get_reverse_sequence(area);
            area.insert(0, "\n>-L-"+s.number +"-"+pqs_number+"\n");

            right_area = new StringBuilder();
            right_area.append(s.sequence, q.end /*q.start + q.end*/, maxJ);//.replace("N", ""));
            right_area = get_reverse_sequence(right_area);

            area.append(right_area);
            if (area.toString().contains("N")) {
                pqs_number++;
                continue;
            }
            Files.write(Paths.get(output_path + "/" + output_name), area.toString().getBytes(), StandardOpenOption.APPEND);

            StringBuilder pqs = new StringBuilder();
            pqs.append(q.sequence);
            //pqs.append(s.sequence, q.start, q.end /*q.start + q.end*/);
            pqs = get_reverse_sequence(pqs);
            pqs.insert(0, "\n>-L-"+s.number +"-"+pqs_number+"\n");
            Files.write(Paths.get(output_path +"/"+outputNamePQS), pqs.toString().getBytes(), StandardOpenOption.APPEND);

            pqs_number++;

        }
    }
}
