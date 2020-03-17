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
        String st = "";
        int counter = 0;
        Cluster c = new Cluster();
        Clusters cs = new Clusters();
        while ((st = br.readLine()) != null &&!Thread.interrupted()) {
            if (!st.isEmpty() && st.charAt(0) == '>') {
                cs.clusters.add(c);
                counter++;
                c = new Cluster();
                c.rows.add(st+"\n");
            } else {
                c.rows.add(st+"\n");
            }
        }
        cs.clusters.remove(0);
        cs.clusters.sort(new ClusterComparator());

        Files.write(Paths.get(outputPath+"/"+outputName), "".getBytes());
        StringBuilder outputString = new StringBuilder();
        Integer i = 0;
        for (Cluster cluster : cs.clusters) {
            cluster.rows.set(0, ">Cluster "+i+"\n");
            outputString.append(String.join("",cluster.rows));
            i++;
        }
        Files.write(Paths.get(outputPath+"/"+outputName), outputString.toString().getBytes(), StandardOpenOption.APPEND);
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
