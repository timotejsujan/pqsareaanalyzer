package external;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Timotej Sujan
 */
public class cluster {
    int max_length = 0;
    ArrayList<String> left_area = new ArrayList<>();
    ArrayList<String> pqs = new ArrayList<>();
    ArrayList<String> right_area = new ArrayList<>();

    public cluster(Integer n, File f) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;

        while ((line = br.readLine()) != null) {

            if (line.equals(">Cluster " + n.toString())) {

                while ((line = br.readLine()) != null && !line.startsWith(">")){

                    String[] temp = line.split(" ");

                    left_area.add(temp[0]);
                    right_area.add(temp[1]);
                    pqs.add(temp[2]);

                    max_length = Math.max(max_length, Math.max(temp[0].length(), temp[2].length()));
                }
                break;
            }
        }
    }
}
