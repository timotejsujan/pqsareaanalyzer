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
        String str;

        while ((str = br.readLine()) != null) {

            if (str.equals(">Cluster " + number.toString())) {

                while ((str = br.readLine()) != null && !str.startsWith(">")){

                    String[] temp = str.split(" ");

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
