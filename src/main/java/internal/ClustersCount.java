package internal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Timotej Sujan
 */
public class ClustersCount {
    private int length = 0;
    private int limit = 0;
    private String inputPath;
    private Map<Integer, Cluster> referenceSeqs = new HashMap();

    public void loadCluster() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(inputPath));
        String st;
        Integer counter = 0;
        int sizeCounter = 0;
        while ((st = br.readLine()) != null) {
            if (st.startsWith(">Cluster ")) {
                if (counter != 0){
                    referenceSeqs.get(counter - 1).size = sizeCounter;
                }
                sizeCounter = 0;
                referenceSeqs.put(counter, new Cluster());
                // delete spaces
                referenceSeqs.get(counter).reference_sequence = br.readLine().replace(" ", "");
                counter++;
            }
            sizeCounter++;
        }
        referenceSeqs.get(counter - 1).size = sizeCounter;
        length = counter;
        limit = counter;
    }

    public static class Cluster{
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
