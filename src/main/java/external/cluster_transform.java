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
        StringBuilder to_write = new StringBuilder();
        while ((st = br.readLine()) != null && !Thread.interrupted()) {
            if (!st.isEmpty() && st.charAt(0) == '>') {
                if (sequenceCounter.get() == 1) {
                    break;
                } else {
                    sequenceCounter.set(0);
                }
                //Files.write(Paths.get(outputPath+"/"+outputName), st.getBytes(), StandardOpenOption.APPEND);
                Files.write(Paths.get(outputPath+"/"+outputName), to_write.toString().getBytes(), StandardOpenOption.APPEND);
                to_write = new StringBuilder();
                st = "\n"+st;
                to_write.append(st);
                continue;
            }
            sequenceCounter.getAndIncrement();
            String name = st.split(">")[1];
            name = name.split("\\.\\.\\.")[0];

            Pqs curr_r = GetSeqByName(">"+name);
            String line = "\n" + curr_r.leftArea + " " + curr_r.rightArea + " " + curr_r.pqs;
            to_write.append(line);
            //Files.write(Paths.get(outputPath+"/"+outputName), line.getBytes(), StandardOpenOption.APPEND);
        }
        if (sequenceCounter.get() != 1){
            Files.write(Paths.get(outputPath+"/"+outputName), to_write.toString().getBytes(), StandardOpenOption.APPEND);
        }
    }

    private Pqs GetSeqByName(String name) throws IOException {

        String[] temp;
        if (name.charAt(1) == '-') {
            name = ">m"+name.substring(2);
        }
        temp = name.split("-", 3);
        Boolean strand = (temp[0].charAt(1) == '+');
        Integer segment = Integer.parseInt(temp[1]);
        Integer area = Integer.parseInt(temp[2]);

        return genom.strands.get(strand).segments.get(segment).sequences.get(area);
    }

    /*
    public void main(String[] args) throws IOException {
        ClusterTransform c = new ClusterTransform();
        c.getClstrFileWithSequences();
    }*/

    private void loadClass() throws IOException {
        genom.strands.put(true, new Strand());
        genom.strands.put(false, new Strand());

        BufferedReader br = new BufferedReader(new FileReader(fasta_file));
        String line = "";
        br.readLine();
        while ((line = br.readLine()) != null &&!Thread.interrupted()) {
            if (!line.isEmpty() && line.charAt(0) == '>') {
                String[] temp;
                if (line.charAt(1) == '-') {
                    line = ">m"+line.substring(2);
                }
                temp = line.split("-", 3);
                Boolean strand = (temp[0].charAt(1) == '+');
                Integer segment = Integer.parseInt(temp[1]);
                Integer area = Integer.parseInt(temp[2]);
                String seq = br.readLine();

                Pqs new_pqs = new Pqs();
                new_pqs.leftArea = seq.substring(0, seq.length()/2);
                new_pqs.rightArea = seq.substring(seq.length()/2);

                if (genom.strands.get(strand).segments.containsKey(segment)) {
                    genom.strands.get(strand).segments.get(segment).sequences.put(area, new_pqs);
                } else {
                    Segment new_segment = new Segment();
                    new_segment.sequences.put(area, new_pqs);
                    genom.strands.get(strand).segments.put(segment, new_segment);
                }
            }
        }
    }

    private void loadPQS() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(pqs_file));
        String line;
        int counter = -1;
        int pos_pqs_number = 0;
        int neg_pqs_number = 0;
        while ((line = br.readLine()) != null && !Thread.interrupted()) {
            if (line.startsWith(";")) {
                counter = Integer.parseInt(line.split(": ")[1]);
                pos_pqs_number = neg_pqs_number = 0;
            } else if (counter != -1 && line.startsWith(">")){
                String[] info = line.split(";");
                Boolean strand = (info[4].split("=")[1].equals("+"));

                String pqs = br.readLine().toUpperCase();
                int pqs_number;
                if (strand) {
                    pqs_number = pos_pqs_number;
                    pos_pqs_number++;
                } else {
                    pqs = get_reverse_sequence(new StringBuilder(pqs)).toString();
                    pqs_number = neg_pqs_number;
                    neg_pqs_number++;
                }

                genom.strands.get(strand).segments.get(counter).
                        sequences.get(pqs_number).pqs = pqs;
            }
            /*
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
            }*/
        }
    }

    private class Genom {
        Map<Boolean, Strand> strands = new HashMap<>();
    }

    private class Strand {
        Map<Integer, Segment> segments = new HashMap<>();
    }

    private class Segment {
        Map<Integer, Pqs> sequences = new HashMap<>();
    }

    private class Pqs {
        String leftArea, pqs, rightArea;
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
}
