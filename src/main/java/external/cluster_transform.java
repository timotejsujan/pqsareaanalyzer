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

public class cluster_transform extends base{

    private final Genom genom = new Genom();
    private final File clstr_file;
    private final File fasta_file;
    private final File pqs_file;
    private int area_size = -1;

    cluster_transform(File cf, File ff, File pqsf, String op, String on){
        clstr_file = cf;
        fasta_file = ff;
        pqs_file = pqsf;
        output_path = op;
        output_name = on;
    }

    // clstr file musí být sorted
    public void start() throws IOException {
        load_class();
        load_pqs();
        BufferedReader br = new BufferedReader(new FileReader(clstr_file));
        Files.createFile(Paths.get(output_path +"/"+ output_name));
        Files.write(Paths.get(output_path +"/"+ output_name), (";area_size="+area_size).getBytes(), StandardOpenOption.APPEND);
        int seq_counter = 0;
        StringBuilder to_write = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null && !Thread.interrupted()) {
            if (line.isEmpty() || line.charAt(0) == ';') continue;

            if (line.charAt(0) == '>') {
                if (seq_counter == 1) {
                    break;
                } else {
                    seq_counter = 0;
                }
                Files.write(Paths.get(output_path +"/"+ output_name), to_write.toString().getBytes(), StandardOpenOption.APPEND);
                to_write = new StringBuilder();
                line = "\n"+line;
                to_write.append(line);
                continue;
            }
            seq_counter++;
            String name = line.split(">")[1];
            name = name.split("\\.\\.\\.")[0];

            Pqs curr_r = get_sequence_using_id(">"+name);
            String pqs_with_area = "\n" + curr_r.leftArea + curr_r.pqs + curr_r.rightArea;
            to_write.append(pqs_with_area);
        }
        if (seq_counter != 1){
            Files.write(Paths.get(output_path +"/"+ output_name), to_write.toString().getBytes(), StandardOpenOption.APPEND);
        }
    }

    private Pqs get_sequence_using_id(String id) {

        String[] temp = id.split(";", 2);
        Boolean strand = (temp[0].charAt(1) == '+');
        Integer segment = Integer.parseInt(temp[0].substring(2));
        Integer area = Integer.parseInt(temp[1]);

        return genom.strands.get(strand).segments.get(segment).sequences.get(area);
    }

    private void load_class() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fasta_file));
        String line;

        while ((line = br.readLine()) != null &&!Thread.interrupted()) {
            if (!line.isEmpty() && line.charAt(0) == '>') {

                String[] temp = line.split(";", 2);
                Boolean strand = (temp[0].charAt(1) == '+');
                Integer segment = Integer.parseInt(temp[0].substring(2));
                Integer area = Integer.parseInt(temp[1]);

                String seq = br.readLine();
                Pqs new_pqs = new Pqs();

                new_pqs.leftArea = seq.substring(0, seq.length()/2);
                new_pqs.rightArea = seq.substring(seq.length()/2);

                if (area_size == -1){
                    area_size = new_pqs.leftArea.length();
                }

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

    private void load_pqs() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(pqs_file));
        String line;
        int segment = -1;
        int pqs_number = 0;
        while ((line = br.readLine()) != null && !Thread.interrupted()) {
            if (line.startsWith(";")) {
                segment = Integer.parseInt(line.split(": ")[1]);
                pqs_number = 0;
            } else if (segment != -1 && line.startsWith(">")){
                String[] info = line.split(";");
                Boolean strand = (info[4].split("=")[1].equals("+"));

                String pqs = br.readLine().toUpperCase();
                if (!strand) {
                    pqs = get_reverse_sequence(new StringBuilder(pqs)).toString();
                }

                genom.strands.get(strand).segments.get(segment).
                        sequences.get(pqs_number++).pqs = pqs;
            }
        }
    }

    private class Genom {
        private class Strand {
            Map<Integer, Segment> segments = new HashMap<>();
        }

        public Genom(){
            strands.put(true, new Strand());
            strands.put(false, new Strand());
        }
        Map<Boolean, Strand> strands = new HashMap<>();
    }

    private class Segment {
        Map<Integer, Pqs> sequences = new HashMap<>();
    }

    private class Pqs {
        String leftArea, pqs, rightArea;
    }
}
