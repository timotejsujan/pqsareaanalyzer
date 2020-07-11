package model;

import javafx.scene.control.TextArea;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class pqsareas extends base {
    private int area_size = 0;
    public String input_path_pqs_positions = "";

    public pqsareas(TextArea outputArea) {
        super(outputArea);
    }


    public boolean is_valid() {
        set_program_path("dummy");
        return is_base_valid() && !input_path_pqs_positions.isEmpty() && area_size > 0;
    }

    public void set_area(int n) {
        area_size = n;
    }

    public void set_input_path_pqs_positions(String inputPath) {
        this.input_path_pqs_positions = inputPath;
    }

    public void start() {

        try {
            Files.createFile(Paths.get(output_path + "/" + output_name + ".txt"));
            get_areas(new File(input_path), new File(input_path_pqs_positions));
        } catch (Exception e) {
            print_status(e.toString());
        }
        print_status("the process has ended");
    }

    private pqs get_pqs(BufferedReader br_pqs, int segment) throws IOException {
        String line = br_pqs.readLine();
        if (line == null) return null;
        if (line.charAt(0) == ';') {
            segment = Integer.parseInt(line.split(": ")[1]);
            line = br_pqs.readLine();
        }
        String[] info_arr = line.split(";");
        int start = Integer.parseInt(info_arr[2].split("=")[1]);
        int end = Integer.parseInt(info_arr[3].split("=")[1]);
        boolean strand = (info_arr[4].split("=")[1].equals("+"));

        line = "";
        int count = 0;
        while (count != end - start + 1){
            line += br_pqs.readLine();
            count = line.length();
            assert (count <= end - start + 1);
        }

        return new pqs(start - 1, end, strand, segment, line);
    }

    private String create_N_string(int length) {
        return "N".repeat(Math.max(0, length));
    }

    private void write_area(pqs curr_pqs, String buffer, int position, int segment, int pqs_number) throws IOException {
        int left_area_start = buffer.length() - position + (curr_pqs.start - area_size);
        int right_area_start = buffer.length() - position + curr_pqs.end;
        assert (curr_pqs.sequence.equals(buffer.substring(left_area_start + area_size, right_area_start)));
        String area = buffer.substring(Math.max(0, left_area_start), left_area_start + area_size).toUpperCase();
        area = create_N_string(area_size - area.length()) + area;
        if (!curr_pqs.strand) area = get_reverse_sequence(new StringBuilder(area)).toString();
        String right_area = buffer.substring(right_area_start, Math.min(buffer.length(), right_area_start + area_size)).toUpperCase();
        right_area = right_area + create_N_string(area_size - right_area.length());
        if (!curr_pqs.strand) right_area = get_reverse_sequence(new StringBuilder(right_area)).toString();
        if (curr_pqs.strand) {
            area += right_area;
        } else {
            area = right_area + area;
        }
        String to_write = ">file=" + output_name + ";strand=" + (curr_pqs.strand ? "+" : "-") + ";segment=" + segment + ";pqs_number=" + (pqs_number) + "\n" + area + "\n";
        Files.write(Paths.get(output_path + "/" + output_name + ".txt"), to_write.getBytes(), StandardOpenOption.APPEND);
    }

    private void get_areas(File genom, File pqs_positions) throws IOException {
        BufferedReader br_g = new BufferedReader(new FileReader(genom));
        BufferedReader br_pqs = new BufferedReader(new FileReader(pqs_positions));
        pqs curr_pqs = get_pqs(br_pqs, -1);
        if (curr_pqs == null) return;
        int curr_max_length = 2 * area_size + curr_pqs.sequence.length();
        StringBuilder buffer = new StringBuilder();
        String line;
        int segment = 0;
        int position = 0;
        int pqs_number = 0;
        while (!Thread.interrupted() && (line = br_g.readLine()) != null) {
            if (line.startsWith(">")) {
                if (segment == curr_pqs.segment) {
                    while (curr_pqs.segment == segment) {
                        write_area(curr_pqs, buffer.toString(), position, segment, pqs_number);
                        pqs_number++;
                        curr_pqs = get_pqs(br_pqs, segment);
                        if (curr_pqs == null) return;
                        curr_max_length = 2 * area_size + curr_pqs.sequence.length();
                    }
                }
                buffer = new StringBuilder();
                segment++;
                position = 0;
                pqs_number = 0;
            } else {
                if (segment == curr_pqs.segment) {
                    position += line.length();
                    while (curr_pqs.segment == segment && (position >= curr_pqs.end + area_size)) {
                        String sequence = buffer + line;
                        write_area(curr_pqs, sequence, position, segment, pqs_number);
                        pqs_number++;
                        curr_pqs = get_pqs(br_pqs, segment);
                        if (curr_pqs == null) return;
                        curr_max_length = 2 * area_size + curr_pqs.sequence.length();
                    }
                }
                buffer.append(line);
                if (buffer.length() > curr_max_length) {
                    buffer = new StringBuilder(buffer.substring(buffer.length() - curr_max_length));
                    assert (buffer.length() == curr_max_length);
                }
            }
        }
    }

    private class pqs {
        public pqs(int s, int e, boolean b, int seg, String seq) {
            segment = seg;
            start = s;
            end = e;
            strand = b;
            sequence = seq;
        }

        int segment;
        int start;
        int end;
        Boolean strand;
        String sequence;
    }
}
