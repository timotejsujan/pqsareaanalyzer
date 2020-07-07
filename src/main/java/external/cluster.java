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
    int area = -1;
    ArrayList<String> left_area = new ArrayList<>();
    ArrayList<String> pqs = new ArrayList<>();
    ArrayList<String> right_area = new ArrayList<>();

    public cluster(Integer n, File f) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(f));
        String line;

        while ((line = br.readLine()) != null) {
            if (line.charAt(0) == ';') {
                String[] area_size = line.split("=");
                assert (area_size[0].equals(";area_size")) : "Missing area_size definition in file (;area_size=<area_size>)";
                area = Integer.parseInt(area_size[1]);
                continue;
            }
            if (area == -1) continue;

            if (line.equals(">cluster=" + n.toString())) {

                while ((line = br.readLine()) != null && !line.startsWith(">")) {
                    String new_pqs = line.substring(area, line.length() - area);

                    left_area.add(line.substring(0, area));
                    pqs.add(new_pqs);
                    right_area.add(line.substring(line.length() - area));

                    max_length = Math.max(max_length, Math.max(area, new_pqs.length()));
                }
                break;
            }
        }
    }
}
