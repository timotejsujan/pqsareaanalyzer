package external;

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
public class cluster_sort {
    public String inputPath;
    public String outputPath;
    public String outputName;

    public void sort() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(inputPath));
        String line = "";
        Cluster clstr = new Cluster();
        Clusters clusters = new Clusters();
        while ((line = br.readLine()) != null &&!Thread.interrupted()) {
            if (!line.isEmpty() && line.charAt(0) == '>') {
                clusters.clusters.add(clstr);
                clstr = new Cluster();
            }
            clstr.rows.add(line+"\n");
        }
        clusters.clusters.remove(0);
        clusters.clusters.sort(new ClusterComparator());

        Files.write(Paths.get(outputPath+"/"+outputName), "".getBytes());
        StringBuilder sorted_clusters = new StringBuilder();
        int i = 0;
        for (Cluster c : clusters.clusters) {
            c.rows.set(0, ">Cluster "+i+"\n");
            sorted_clusters.append(String.join("", c.rows));
            i++;
        }
        Files.write(Paths.get(outputPath+"/"+outputName), sorted_clusters.toString().getBytes(), StandardOpenOption.APPEND);
    }

    private class Clusters
    {
        ArrayList<Cluster> clusters = new ArrayList<>();
    }

    private class Cluster{
        ArrayList<String> rows = new ArrayList<>();
    }

    public class ClusterComparator implements Comparator<Cluster> {
        @Override
        public int compare(Cluster o1, Cluster o2) {
            return (-1)*Integer.compare(o1.rows.size(), o2.rows.size());
        }
    }
}
