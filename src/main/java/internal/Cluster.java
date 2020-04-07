package internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Timotej Sujan
 */
public class Cluster {
    int maxLength = 0;
    ArrayList<String> leftArea = new ArrayList<>();
    ArrayList<String> pqs = new ArrayList<>();
    ArrayList<String> rightArea = new ArrayList<>();

    public Cluster(Integer number, File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        while ((st = br.readLine()) != null) {
            if (st.equals(">Cluster " + number.toString())) {
                while ((st = br.readLine()) != null && !st.startsWith(">")){
                    String[] temp = st.split(" ");
                    leftArea.add(temp[0]);
                    rightArea.add(temp[1]);
                    pqs.add(temp[2]);
                    maxLength = Math.max(maxLength, Math.max(temp[0].length(), temp[2].length()));
                }
                break;
            }
        }
    }
}
