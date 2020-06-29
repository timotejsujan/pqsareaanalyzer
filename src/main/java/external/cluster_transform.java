package external;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Timotej Sujan
 */



public class cluster_transform {

    private Genom genom = new Genom();

    public File clstr_file;
    public File fasta_file;
    public File pqs_file;
    public String outputPath;
    public String outputName;

    // clstr file musí být sorted
    public void getClstrFileWithSequences() throws IOException {
        loadClass();
        loadPQS();
        BufferedReader br = new BufferedReader(new FileReader(clstr_file));
        String st;
        st = br.readLine();
        Files.write(Paths.get(outputPath+"/"+outputName), st.getBytes());
        AtomicInteger sequenceCounter = new AtomicInteger();
        while ((st = br.readLine()) != null && !Thread.interrupted()) {
            if (!st.isEmpty() && st.charAt(0) == '>') {
                if (sequenceCounter.get() == 1) {
                    break;
                } else {
                    sequenceCounter.set(0);
                }
                st = "\n"+st;
                Files.write(Paths.get(outputPath+"/"+outputName), st.getBytes(), StandardOpenOption.APPEND);
                continue;
            }
            sequenceCounter.getAndIncrement();
            String name = st.split(">")[1];
            name = name.split("\\.\\.\\.")[0];

            Row curr_r = GetSeqByName(">"+name);
            String line = "\n" + curr_r.leftArea + " " + curr_r.rightArea + " " + curr_r.pqs;

            Files.write(Paths.get(outputPath+"/"+outputName), line.getBytes(), StandardOpenOption.APPEND);
        }
    }

    private Row GetSeqByName(String name) throws IOException {

        String[] temp;
        if (name.charAt(1) == '-') {
            name = ">m"+name.substring(2);
        }
        temp = name.split("-", 3);
        Character strand = (temp[0].charAt(1) == '+'? '+' : '-');
        Character side = temp[0].charAt(2);
        String scaffold = temp[1];
        String area = temp[2];

        return genom.strands.get(strand).sides.get(side).scaffolds.get(scaffold).sequences.get(area);
    }

    /*
    public void main(String[] args) throws IOException {
        ClusterTransform c = new ClusterTransform();
        c.getClstrFileWithSequences();
    }*/

    private void loadClass() throws IOException {
        genom.strands.put('+', new Strand());
        genom.strands.put('-', new Strand());
        genom.strands.get('+').sides.put('R', new Side());
        genom.strands.get('-').sides.put('R', new Side());
        genom.strands.get('+').sides.put('L', new Side());
        genom.strands.get('-').sides.put('L', new Side());

        BufferedReader br = new BufferedReader(new FileReader(fasta_file));
        String str = "";
        br.readLine();
        while ((str = br.readLine()) != null &&!Thread.interrupted()) {
            if (!str.isEmpty() && str.charAt(0) == '>') {
                String[] temp;
                if (str.charAt(1) == '-') {
                    str = ">m"+str.substring(2);
                }
                temp = str.split("-", 3);
                Character strand = (temp[0].charAt(1) == '+'? '+' : '-');
                Character side = temp[0].charAt(2);
                String scaffold = temp[1];
                String area = temp[2];
                String seq = br.readLine();

                Row new_row = new Row();
                new_row.leftArea = seq.substring(0, seq.length()/2);
                new_row.rightArea = seq.substring(seq.length()/2);

                if (genom.strands.get(strand).sides.get(side).scaffolds.containsKey(scaffold)) {
                    genom.strands.get(strand).sides.get(side).scaffolds.get(scaffold).sequences.put(area, new_row);
                } else {
                    Scaffold new_scaffold = new Scaffold();

                    new_scaffold.sequences.put(area, new_row);
                    genom.strands.get(strand).sides.get(side).scaffolds.put(scaffold, new_scaffold);
                }
            }
        }
    }

    private void loadPQS() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(pqs_file));
        String st = "";
        br.readLine();
        while ((st = br.readLine()) != null && !Thread.interrupted()) {
            if (!st.isEmpty() && st.charAt(0) == '>') {
                String[] temp;
                if (st.charAt(1) == '-') {
                    st = ">m" + st.substring(2);
                }
                temp = st.split("-", 3);
                Character strand = (temp[0].charAt(1) == '+' ? '+' : '-');
                Character side = temp[0].charAt(2);
                String scaffold = temp[1];
                String area = temp[2];

                genom.strands.get(strand).sides.get(side).scaffolds.get(scaffold).sequences.get(area).pqs = br.readLine();
            }
        }
    }

    private class Genom {
        Map<Character, Strand> strands = new HashMap<>();
    }

    private class Strand {
        Map<Character, Side> sides = new HashMap<>();

    }

    private class Side {
        Map<String, Scaffold> scaffolds = new HashMap<>();
    }

    private class Scaffold {
        Map<String, Row> sequences = new HashMap<>();
    }

    private class Row {
        String leftArea;
        String pqs;
        String rightArea;
    }
}
