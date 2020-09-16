package model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Timotej Sujan
 */

public class cluster_transform extends base {

    private final dataset dataset = new dataset();
    private final dataset dataset_2 = new dataset();
    private final File cluster_file;
    private final File pqsareas_output_file;
    private final File pqsfinder_output_file;
    private File pqsareas_output_file_2;
    private File pqsfinder_output_file_2;
    private int area_size = -1;
    private String file_name_1;
    private String file_name_2;
    cdhit2D cdhit2d = null;

    cluster_transform(cdhit c, cluster_sort cs) throws Exception {
        cluster_file = new File(Paths.get(cs.output_path, cs.output_name).toString());
        pqsareas_output_file = new File(c.input_path);
        pqsfinder_output_file = new File(c.input_path_pqs);
        output_path = c.output_path;
        output_name = c.output_name + ".txt";

        load_areas(pqsareas_output_file, dataset, true);
        load_pqs(pqsfinder_output_file, dataset);
    }

    cluster_transform(cdhit2D c, cluster_sort cs) throws Exception {
        cluster_file = new File(Paths.get(cs.output_path, cs.output_name).toString());
        pqsareas_output_file = new File(c.input_path);
        pqsfinder_output_file = new File(c.input_path_pqs_1);
        pqsareas_output_file_2 = new File(c.input_path_areas_2);
        pqsfinder_output_file_2 = new File(c.input_path_pqs_2);
        output_path = c.output_path;
        output_name = c.output_name + ".txt";
        cdhit2d = c;

        load_areas(pqsareas_output_file, dataset, true);
        load_pqs(pqsfinder_output_file, dataset);
        load_areas(pqsareas_output_file_2, dataset_2, false);
        load_pqs(pqsfinder_output_file_2, dataset_2);
    }

    // clstr file musí být sorted
    public void start() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(cluster_file));
        Files.createFile(Paths.get(Paths.get(output_path, output_name).toString()));
        Files.write(Paths.get(Paths.get(output_path, output_name).toString()),
                (";area_size=" + area_size).getBytes(), StandardOpenOption.APPEND);
        StringBuilder to_write = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null && !Thread.interrupted()) {
            if (line.isEmpty() || line.charAt(0) == ';') continue;

            if (line.charAt(0) == '>') {

                Files.write(Paths.get(Paths.get(output_path, output_name).toString()),
                        to_write.toString().getBytes(), StandardOpenOption.APPEND);
                to_write = new StringBuilder();
                line = "\n" + line;
                to_write.append(line);
                continue;
            }
            String name = line.split(">")[1];
            name = name.split("\\.\\.\\.")[0];
            pqs_with_area curr_r = get_sequence_using_id(">" + name);
            String pqs_with_area = "\n" + curr_r.leftArea + curr_r.pqs + curr_r.rightArea;
            to_write.append(pqs_with_area);
        }
        Files.write(Paths.get(Paths.get(output_path, output_name).toString()),
                to_write.toString().getBytes(), StandardOpenOption.APPEND);
    }

    private pqs_with_area get_sequence_using_id(String id) {

        String[] info = id.split(";");
        String file_name = info[0].split("=")[1];
        Boolean strand = (info[1].split("=")[1].equals("+"));
        Integer segment = Integer.parseInt(info[2].split("=")[1]);
        Integer pqs_number = Integer.parseInt(info[3].split("=")[1]);

        dataset db = (file_name_1.equals(file_name) ? dataset : dataset_2);
        return db.strands.get(strand).segments.get(segment).sequences.get(pqs_number);
    }

    private void load_areas(File pqsareas_output, dataset db, boolean first) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(pqsareas_output));
        String line;

        boolean file_name_is_set = false;

        while ((line = br.readLine()) != null && !Thread.interrupted()) {
            if (!line.isEmpty() && line.charAt(0) == '>') {
                String[] info = line.split(";");

                if (!file_name_is_set) {
                    String file_name = info[0].split("=")[1];
                    if (first) {
                        file_name_1 = file_name;
                    } else {
                        file_name_2 = file_name;
                    }
                    file_name_is_set = true;
                }
                Boolean strand = (info[1].split("=")[1].equals("+"));
                Integer segment = Integer.parseInt(info[2].split("=")[1]);
                Integer pqs_number = Integer.parseInt(info[3].split("=")[1]);

                String seq = br.readLine();
                pqs_with_area new_pqs = new pqs_with_area();

                new_pqs.leftArea = seq.substring(0, seq.length() / 2);
                new_pqs.rightArea = seq.substring(seq.length() / 2);

                if (area_size == -1) {
                    area_size = new_pqs.leftArea.length();
                } else if (area_size != new_pqs.leftArea.length()){
                    if (cdhit2d != null){
                        cdhit2d.print_status("The two input datasets have different size of areas, " +
                                "it won't be possible to display sequence logo!!!");
                        cdhit2d = null;
                    }
                }

                if (db.strands.get(strand).segments.containsKey(segment)) {
                    db.strands.get(strand).segments.get(segment).sequences.put(pqs_number, new_pqs);
                } else {
                    cluster_transform.segment new_segment = new segment();
                    new_segment.sequences.put(pqs_number, new_pqs);
                    db.strands.get(strand).segments.put(segment, new_segment);
                }
            }
        }
        if (file_name_1.equals(file_name_2)) throw new Exception("File attributes (>file=...) in pqsareas outputs description lines have to be different in each file");
    }

    private void load_pqs(File pqs_file, dataset db) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(pqs_file));
        String line;
        int segment = -1;
        int pqs_number = 0;
        while ((line = br.readLine()) != null && !Thread.interrupted()) {
            if (line.startsWith(";")) {
                segment = Integer.parseInt(line.split(": ")[1]);
                pqs_number = 0;
            } else if (segment != -1 && line.startsWith(">")) {
                String[] info = line.split(";");
                Boolean strand = (info[4].split("=")[1].equals("+"));

                String pqs = br.readLine().toUpperCase();
                if (!strand) {
                    pqs = get_reverse_sequence(new StringBuilder(pqs)).toString();
                }

                db.strands.get(strand).segments.get(segment).
                        sequences.get(pqs_number++).pqs = pqs;
            }
        }
    }

    private class dataset {
        private class strand {
            Map<Integer, segment> segments = new HashMap<>();
        }

        public dataset() {
            strands.put(true, new strand());
            strands.put(false, new strand());
        }

        Map<Boolean, strand> strands = new HashMap<>();
    }

    private class segment {
        Map<Integer, pqs_with_area> sequences = new HashMap<>();
    }

    private class pqs_with_area {
        String leftArea, pqs, rightArea;
    }
}
