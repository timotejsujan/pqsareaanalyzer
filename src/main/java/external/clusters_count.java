package external;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Timotej Sujan
 */
public class clusters_count {
    // private
    private int length = 0;
    private int limit = 0;
    private String inputPath;
    private Map<Integer, Cluster> referenceSeqs = new HashMap();

    // public
    public void load_cluster() throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(inputPath));

        String line;
        int counter = 0;
        int cluster_size = 0;

        while ((line = br.readLine()) != null) {
            if (line.startsWith(">Cluster ")) {
                if (counter != 0){
                    referenceSeqs.get(counter - 1).size = cluster_size;
                }
                cluster_size = 0;
                referenceSeqs.put(counter, new Cluster());
                // delete spaces
                referenceSeqs.get(counter).reference_sequence = br.readLine();
                //referenceSeqs.get(counter).reference_sequence = br.readLine().replace(" ", "");
                counter++;
            }
            cluster_size++;
        }
        referenceSeqs.get(counter - 1).size = cluster_size;
        length = counter;
        limit = counter;
    }

    public class Cluster{
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
