package internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Timotej Sujan
 */
public class ClustersCount {
    private int length = 0;
    private int limit = 0;
    private String inputPath;
    private String inputPathPqs;

    public int getLength() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(inputPath));
        String st;
        while ((st = br.readLine()) != null) {
            if (st.startsWith(">Cluster ")) {
                length++;
            }
        }
        return length;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public void setInputPathPqs(String inputPathPqs) {
        this.inputPathPqs = inputPathPqs;
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

    public String getInputPathPqs() {
        return inputPathPqs;
    }
}
