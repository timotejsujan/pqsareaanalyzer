package controller;

import model.clusters_compare;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Timotej Sujan
 */
public class clusters_compare_controller implements Initializable {
    private final FileChooser file_chooser = new FileChooser();
    private final DirectoryChooser dir_chooser = new DirectoryChooser();

    @FXML
    private TextField input_path_clusters_1, input_path_clusters_2, output_name, output_dir;
    @FXML
    private TextField distance;
    private model.clusters_compare clusters_compare;
    base_controller contr;

    public void show() {
        clusters_compare.set_distance(Integer.parseInt(distance.getText()));
        clusters_compare.set_output_name(output_name.getText().replace(" ", "_"));
        output_name.setText(output_name.getText());

        if (!clusters_compare.is_valid()) {
            //clusters_compare.print_stream.println("values are NOT valid, something is missing!");
            return;
        }
        clusters_compare.show();
    }

    public void export() {
        clusters_compare.export();
    }

    public void set_input_path_cluster_1() {
        File file = file_chooser.showOpenDialog(Main.primary_stage);
        if (file != null) {
            input_path_clusters_1.setText(".../"+file.getName());
            clusters_compare.set_input_path(file.getAbsolutePath());
        }
    }

    public void set_input_path_cluster_1(String path, String name){
        clusters_compare.input_path = path;
        input_path_clusters_1.setText(".../"+name);
    }

    public void set_input_path_cluster_2() {
        File file = file_chooser.showOpenDialog(Main.primary_stage);
        if (file != null) {
            input_path_clusters_2.setText(".../"+file.getName());
            clusters_compare.set_input_path_pqs_positions(file.getAbsolutePath());
        }
    }

    public void set_output_dir() throws IOException {
        File file = dir_chooser.showDialog(Main.primary_stage);
        if (file != null) {
            output_dir.setText(".../"+file.getName());
            clusters_compare.set_output_dir(file.getAbsolutePath());
        }
    }

    public void init_config(){
        if (Main.config.output_dir_name != null && Main.config.output_dir != null) {
            output_dir.setText(".../"+Main.config.output_dir_name);
            clusters_compare.output_path = Main.config.output_dir;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clusters_compare = new clusters_compare(new TextArea());
        init_config();
    }
}
