package internal;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Timotej Sujan
 */



public class ClusterTransform {
    public File clstr_file = new File("/Users/timotej.sujan/Desktop/clustertransform/sort");
    public File fasta_file = new File("/Users/timotej.sujan/Desktop/clustertransform/test.txt");
    public String outputPath = "/Users/timotej.sujan/Desktop/clustertransform";
    public String outputName = "example_output.txt";
    private Genom genom = new Genom();

    // clstr file musí být sorted
    public void getClstrFileWithSequences() throws IOException {
        loadClass();
        BufferedReader br = new BufferedReader(new FileReader(clstr_file));
        String st;
        st = br.readLine();
        Files.write(Paths.get(outputPath+"/"+outputName), st.getBytes());
        int sequenceCounter = 0;
        while ((st = br.readLine()) != null && !Thread.interrupted()) {
            if (!st.isEmpty() && st.charAt(0) == '>') {
                if (sequenceCounter == 1) {
                    break;
                } else {
                    sequenceCounter = 0;
                }
                st = "\n"+st;
                Files.write(Paths.get(outputPath+"/"+outputName), st.getBytes(), StandardOpenOption.APPEND);
                continue;
            }
            sequenceCounter++;
            String name = st.split(">")[1];
            name = name.split("\\.\\.\\.")[0];
            name = GetSeqByName(">"+name);
            name = "\n"+name;
            Files.write(Paths.get(outputPath+"/"+outputName), name.getBytes(), StandardOpenOption.APPEND);
        }
        Cluster c = new Cluster(0, new File(outputPath+"/"+outputName));

    }

    private String GetSeqByName(String name) throws IOException {

        String[] temp;
        if (name.charAt(1) == '-') {
            name = ">m"+name.substring(2, name.length());
        }
        temp = name.split("-", 3);
        Character strand = (temp[0].charAt(1) == '+'? '+' : '-');
        Character side = temp[0].charAt(2);
        String scaffold = temp[1];
        String area = temp[2];

        return genom.strands.get(strand).sides.get(side).scaffolds.get(scaffold).sequences.get(area);
    }

    public static void main(String[] args) throws IOException {
        ClusterTransform c = new ClusterTransform();
        c.getClstrFileWithSequences();
    }

    private void loadClass() throws IOException {
        genom.strands.put('+', new Strand());
        genom.strands.put('-', new Strand());
        genom.strands.get('+').sides.put('R', new Side());
        genom.strands.get('-').sides.put('R', new Side());
        genom.strands.get('+').sides.put('L', new Side());
        genom.strands.get('-').sides.put('L', new Side());

        BufferedReader br = new BufferedReader(new FileReader(fasta_file));
        String st = "";
        st = br.readLine();
        while ((st = br.readLine()) != null &&!Thread.interrupted()) {
            if (!st.isEmpty() && st.charAt(0) == '>') {
                String[] temp;
                if (st.charAt(1) == '-') {
                    st = ">m"+st.substring(2, st.length());
                }
                temp = st.split("-", 3);
                Character strand = (temp[0].charAt(1) == '+'? '+' : '-');
                Character side = temp[0].charAt(2);
                String scaffold = temp[1];
                String area = temp[2];
                String seq = br.readLine();

                if (genom.strands.get(strand).sides.get(side).scaffolds.containsKey(scaffold)) {
                    genom.strands.get(strand).sides.get(side).scaffolds.get(scaffold).sequences.put(area, seq);
                } else {
                    Scaffold new_scaffold = new Scaffold();
                    new_scaffold.sequences.put(area, seq);
                    genom.strands.get(strand).sides.get(side).scaffolds.put(scaffold, new_scaffold);
                }
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
        Map<String, String> sequences = new HashMap<>();
    }
}
