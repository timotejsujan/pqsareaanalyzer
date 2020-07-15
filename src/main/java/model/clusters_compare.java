package model;

import javafx.scene.control.TextArea;
import javafx.util.Pair;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;

public class clusters_compare extends base {
    private int threshold = -1;
    public String input_path_clusters_2 = "";

    private ArrayList<cluster> cluster_1;
    private ArrayList<cluster> cluster_2;

    private ArrayList<Pair<cluster, cluster>> compared;

    public clusters_compare(TextArea outputArea) {
        super(outputArea);
    }


    public boolean is_valid() {
        set_program_path("dummy");
        return is_base_valid() && !input_path_clusters_2.isEmpty() && threshold >= 0;
    }

    public void set_distance(int n) {
        threshold = n;
    }

    public void set_input_path_pqs_positions(String inputPath) {
        this.input_path_clusters_2 = inputPath;
    }

    public void load_clusters() throws IOException {
        cluster_1 = load_cluster(input_path);
        cluster_2 = load_cluster(input_path_clusters_2);
    }

    private ArrayList<cluster> load_cluster(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        ArrayList<cluster> clusters = new ArrayList<>();

        String line;
        int cluster_size = 0;
        cluster curr_c = null;
        while ((line = br.readLine()) != null) {
            if (line.startsWith(">")) {
                if (curr_c != null){
                    curr_c.size = cluster_size;
                    clusters.add(curr_c);
                }
                cluster_size = 0;
                curr_c = new cluster();
                curr_c.index = Integer.parseInt(line.split("=")[1]);
                curr_c.reference_sequence = br.readLine();
            }
            cluster_size++;
        }
        return clusters;
    }

    public void show() throws IOException {
        load_clusters();
        compare();
    }

    public void export() throws IOException {
        load_clusters();
        compare();
        compared.sort(new ClusterComparator());
        Files.createFile(Paths.get(output_path + "/" + output_name + ".txt"));
        StringBuilder to_write = new StringBuilder();
        to_write.append("upper clusters are from file ").append(Paths.get(input_path).getFileName()).append("\n");
        to_write.append("bottom clusters are from file ").append(Paths.get(input_path_clusters_2).getFileName()).append("\n");
        to_write.append("levenshtein distance threshold is ").append(threshold).append("\n\n");

        for (Pair<cluster, cluster> p : compared){
            cluster left = p.getKey();
            cluster right = p.getValue();
            to_write.append("cluster=").append(left.index).append(";size=").append(left.size)
                    .append(" with ref. seq.").append("\n");
            to_write.append(left.reference_sequence).append("\n");
            to_write.append("is similar to\n");
            to_write.append("cluster=").append(right.index).append(";size=").append(right.size)
                    .append(" with ref. seq.").append("\n");
            to_write.append(left.reference_sequence).append("\n\n");
        }
        Files.write(Paths.get(output_path + "/" + output_name + ".txt"), to_write.toString().getBytes(), StandardOpenOption.APPEND);
    }

    private void compare(){
        compared = new ArrayList<>();
        LevenshteinDistance ld = new LevenshteinDistance();
        for (cluster c_1 : cluster_1){
            for (cluster c_2 : cluster_2){
                if (ld.apply(c_1.reference_sequence, c_2.reference_sequence) <= threshold){
                    compared.add(new Pair<>(c_1, c_2));
                }
            }
        }
    }

    private class cluster{
        int index;
        int size;
        String reference_sequence;
    }

    private class ClusterComparator implements Comparator<Pair<cluster, cluster>> {
        @Override
        public int compare(Pair<cluster, cluster> o1, Pair<cluster, cluster> o2) {
            int size_1 = o1.getKey().size + o1.getValue().size;
            //int diff_1 = Math.abs(o1.getKey().size - o1.getValue().size);
            int size_2 = o2.getKey().size + o2.getValue().size;
            //int diff_2 = Math.abs(o2.getKey().size - o2.getValue().size);
            return (-1) * Integer.compare(size_1, size_2);
        }
    }

}
