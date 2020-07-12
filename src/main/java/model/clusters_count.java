package model;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Timotej Sujan
 */
public class clusters_count {
    // private
    private int length = 0;
    private int limit = 0;
    public int area = -1;
    private String inputPath;
    private final Map<Integer, Cluster> referenceSeqs = new HashMap<>();

    // public
    public void load_cluster() throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(inputPath));

        String line;
        int counter = 0;
        int cluster_size = 0;

        while ((line = br.readLine()) != null) {
            if (line.charAt(0) == ';') {
                String[] area_size = line.split("=");
                assert (area_size[0].equals(";area_size")) : "Missing area_size definition in file (;area_size=<area_size>)";
                area = Integer.parseInt(area_size[1]);
                continue;
            }
            if (line.startsWith(">")) {
                if (counter != 0) {
                    referenceSeqs.get(counter - 1).size = cluster_size;
                }
                cluster_size = 0;
                referenceSeqs.put(counter, new Cluster());
                referenceSeqs.get(counter).reference_sequence = br.readLine();
                counter++;
            }
            cluster_size++;
        }
        referenceSeqs.get(counter - 1).size = cluster_size;
        length = counter;
        limit = counter;
    }

    private ArrayList<Pair<Integer, Integer>> get_columns(String columns){
        ArrayList<Pair<Integer, Integer>> cols = new ArrayList<>();
        String[] splitted = columns.split(",");
        for (String s : splitted){
            String[] col = s.split("-");
            int first = Integer.parseInt(col[0]);
            int second;
            if(col.length == 2){
                second = (col[1].equals("up") ? Integer.MAX_VALUE : Integer.parseInt(col[1]));
            } else {
                second = first;
            }
            cols.add(new Pair<>(first, second));
        }
        return cols;
    }

    public void export_table(File file, String columns) throws IOException {
        ArrayList<Pair<Integer, Integer>> cols = get_columns(columns);
        if (cols.isEmpty()) return;
        Collections.reverse(cols);
        StringBuilder table = new StringBuilder();
        table.append("Size #seq #clstr\n");
        int total_seq = 0;
        int total_clstr = 0;
        int count_seq = 0;
        int count_clstr = 0;
        int col_number = 0;
        int first = cols.get(col_number).getKey();
        int second = cols.get(col_number).getKey();
        for (Map.Entry<Integer, Cluster> entry : referenceSeqs.entrySet()){
            int size = entry.getValue().size;
            first = cols.get(col_number).getKey();
            second = cols.get(col_number).getValue();
            while (!(size >= first && size <= second)) {
                table.append(first);
                table.append((second == Integer.MAX_VALUE ? "-up " : "-" + second + " "));
                table.append(count_seq).append(" ").append(count_clstr).append("\n");
                count_clstr = 0;
                count_seq = 0;
                col_number++;
                first = cols.get(col_number).getKey();
                second = cols.get(col_number).getValue();
            }
            count_clstr++;
            total_clstr++;
            count_seq += size;
            total_seq += size;
        }

        table.append(first);
        table.append((second == Integer.MAX_VALUE ? "-up " : "-" + second + " "));
        table.append(count_seq).append(" ").append(count_clstr).append("\n");

        for (int i = col_number + 1; i < cols.size(); i++){
            first = cols.get(i).getKey();
            second = cols.get(i).getKey();
            table.append(first);
            table.append((second == Integer.MAX_VALUE ? "-up " : "-" + second + " "));
            table.append(0).append(" ").append(0).append("\n");
        }

        table.append("Total ").append(total_seq).append(" ").append(total_clstr).append("\n");
        String table_s = align_to_table(table.toString());
        Files.write(Paths.get(file.getAbsolutePath()), table_s.getBytes());
    }

    public String align_to_table(String s){
        int max_first = 0;
        int max_second = 0;
        int max_third = 0;
        String[] rows = s.split("\n");
        for (String r : rows){
            String[] splitted = r.split(" ");
            max_first = Integer.max(max_first, splitted[0].length());
            max_second = Integer.max(max_first, splitted[1].length());
            max_third = Integer.max(max_first, splitted[2].length());
        }
        ArrayList<String> result = new ArrayList<>();
        for (String r : rows){
            String[] splitted = r.split(" ");
            splitted[0] += " ".repeat(max_first - splitted[0].length());
            splitted[1] += " ".repeat(max_second - splitted[1].length());
            splitted[2] += " ".repeat(max_third - splitted[2].length());
            result.add(String.join(" ", splitted));
        }
        return String.join("\n", result);
    }

    public class Cluster {
        public int size = 0;
        public String reference_sequence = "";
    }

    public int getLength() {
        return length;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public String getInputPath() {
        return inputPath;
    }

    public Map<Integer, Cluster> getReferenceSeqs() {
        return referenceSeqs;
    }
}
