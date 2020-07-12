package model;

import javafx.scene.control.TextArea;

public class clusters_compare extends base {
    private int lev_distance = 0;
    public String input_path_clusters_2 = "";

    public clusters_compare(TextArea outputArea) {
        super(outputArea);
    }


    public boolean is_valid() {
        set_program_path("dummy");
        return is_base_valid() && !input_path_clusters_2.isEmpty() && lev_distance > 0;
    }

    public void set_distance(int n) {
        lev_distance = n;
    }

    public void set_input_path_pqs_positions(String inputPath) {
        this.input_path_clusters_2 = inputPath;
    }

    public void show() {

    }

    public void export() {

    }


}
