package internal;

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
public class ClusterSort {
    public String inputPath;
    public String outputPath;
    public String outputName;

    public void clusterSort() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(inputPath));
        String str = "";
        Cluster clstr = new Cluster();
        Clusters clusters = new Clusters();
        while ((str = br.readLine()) != null &&!Thread.interrupted()) {
            if (!str.isEmpty() && str.charAt(0) == '>') {
                clusters.clusters.add(clstr);
                clstr = new Cluster();
                clstr.rows.add(str+"\n");
            } else {
                clstr.rows.add(str+"\n");
            }
        }
        clusters.clusters.remove(0);
        clusters.clusters.sort(new ClusterComparator());

        Files.write(Paths.get(outputPath+"/"+outputName), "".getBytes());
        StringBuilder outputString = new StringBuilder();
        int i = 0;
        for (Cluster cluster : clusters.clusters) {
            cluster.rows.set(0, ">Cluster "+i+"\n");
            outputString.append(String.join("", cluster.rows));
            i++;
        }
        Files.write(Paths.get(outputPath+"/"+outputName), outputString.toString().getBytes(), StandardOpenOption.APPEND);
    }

    private static class Clusters
    {
        ArrayList<Cluster> clusters = new ArrayList<>();
    }

    private static class Cluster{
        ArrayList<String> rows = new ArrayList<>();
    }

    public static class ClusterComparator implements Comparator<Cluster> {
        @Override
        public int compare(Cluster o1, Cluster o2) {
            return (-1)*Integer.compare(o1.rows.size(), o2.rows.size());
        }
    }
}
