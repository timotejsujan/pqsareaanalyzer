package model;

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
        int area_size = -1;

        while ((line = br.readLine()) != null) {
            if (line.charAt(0) == ';') {
                String[] area_size_s = line.split("=");
                assert (area_size_s[0].equals(";area_size")) : "Missing area_size definition in file (;area_size=<area_size>)";
                area_size = Integer.parseInt(area_size_s[1]);
                continue;
            }
            if (area_size == -1) continue;

            if (line.equals(">cluster=" + n.toString())) {

                while ((line = br.readLine()) != null && !line.startsWith(">")) {
                    String new_pqs = line.substring(area_size, line.length() - area_size);

                    left_area.add(line.substring(0, area_size));
                    pqs.add(new_pqs);
                    right_area.add(line.substring(line.length() - area_size));

                    max_length = Math.max(max_length, Math.max(area_size, new_pqs.length()));
                }
                break;
            }
        }
    }
}
