package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author Timotej Sujan
 */
public class cluster_sort extends base {

    boolean keep_one_sized;

    public cluster_sort(cdhit c) {
        input_path = Paths.get(c.output_path, c.output_name + c.name_suffix + ".clstr").toString();
        output_path = c.output_path;
        output_name = c.output_name + c.name_suffix + "_sort.clstr";
        keep_one_sized = c.keep_one_sized;
    }

    public cluster_sort(cdhit2D c) {
        input_path = Paths.get(c.output_path, c.output_name + c.name_suffix + ".clstr").toString();
        output_path = c.output_path;
        output_name = c.output_name + c.name_suffix + "_sort.clstr";
        keep_one_sized = c.keep_one_sized;
    }

    public void sort() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(input_path));
        String line;
        Cluster clstr = new Cluster();
        ArrayList<Cluster> clusters = new ArrayList<>();
        while ((line = br.readLine()) != null && !Thread.interrupted()) {
            if (!line.isEmpty() && line.charAt(0) == '>') {
                clusters.add(clstr);
                clstr = new Cluster();
                clstr.rows.add(line + "\n");
            } else if (line.endsWith("*")){
                clstr.rows.add(1, line + "\n");
            } else {
                clstr.rows.add(line + "\n");
            }
        }
        clusters.remove(0);
        clusters.sort(new ClusterComparator());

        Files.createFile(Paths.get(output_path, output_name));
        StringBuilder sorted_clusters = new StringBuilder();
        int i = 0;
        for (Cluster c : clusters) {
            if (!keep_one_sized && c.rows.size() <= 2) break;
            c.rows.set(0, ">cluster=" + (i++) + "\n");
            sorted_clusters.append(String.join("", c.rows));
        }
        Files.write(Paths.get(Paths.get(output_path, output_name).toString()), sorted_clusters.toString().getBytes(), StandardOpenOption.APPEND);
    }

    private class Cluster {
        ArrayList<String> rows = new ArrayList<>();
    }

    public class ClusterComparator implements Comparator<Cluster> {
        @Override
        public int compare(Cluster o1, Cluster o2) {
            return (-1) * Integer.compare(o1.rows.size(), o2.rows.size());
        }
    }
}
