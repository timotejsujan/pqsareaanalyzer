package internal;

import java.io.*;
import java.util.ArrayList;

/**
 * @author Timotej Sujan
 */
public class Cluster {
    ArrayList<String> sequences = new ArrayList<>();

    public Cluster(Integer number, File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        while ((st = br.readLine()) != null) {
            if (st.equals(">Cluster " + number.toString())) {
                while (!(st = br.readLine()).startsWith(">")){
                    sequences.add(st);
                }
                return;
            }
        }
    }
}
