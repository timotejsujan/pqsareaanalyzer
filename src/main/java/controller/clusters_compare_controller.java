package controller;

import javafx.scene.control.Button;
import model.clusters_compare;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

/**
 * @author Timotej Sujan
 */
public class clusters_compare_controller extends helper implements Initializable {
    private final FileChooser file_chooser = new FileChooser();
    private final DirectoryChooser dir_chooser = new DirectoryChooser();
    public Button output_dir_btn;
    public Button input_path_clusters_1_btn;
    public Button input_path_clusters_2_btn;

    @FXML private TextField input_path_clusters_1, input_path_clusters_2, output_name, output_dir;
    @FXML private TextField distance;
    private clusters_compare clusters_compare;

    public void start() {
        if(distance.getText().isEmpty()){
            clusters_compare.print_stream.println("values are NOT valid, something is missing!");
            return;
        }
        clusters_compare.set_distance(Integer.parseInt(distance.getText()));
        clusters_compare.set_output_name(output_name.getText());

        if (!clusters_compare.is_valid()) {
            clusters_compare.print_stream.println("values are NOT valid, something is missing!");
            return;
        }

        exec_service = Executors.newSingleThreadExecutor();

        exec_service.execute(() -> {
            try {
                clusters_compare.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            exec_service.shutdownNow();
        });
        switch_disabled();
        java.util.Date date = new java.util.Date();
        clusters_compare.print_stream.println(date.toString() + " the process has started");
        Runnable pqsareas_runnable = () -> {
            if (!exec_service.isShutdown()) {
                java.util.Date date1 = new java.util.Date();
                clusters_compare.print_stream.println(date1.toString() + " the process is running");
            } else {
                timeline.stop();
                switch_disabled();
            }
        };
        timeline = create_timeline(5, pqsareas_runnable);
        timeline.play();
    }

    public void stop() {
        java.util.Date date = new java.util.Date();
        clusters_compare.print_stream.println(date.toString() + " the process has been stopped externally");
        exec_service.shutdownNow();
    }

    private void switch_disabled(){
        start_btn.setDisable(!start_btn.isDisable());
        stop_btn.setDisable(!stop_btn.isDisable());
        input_path_clusters_1.setDisable(!input_path_clusters_1.isDisable());
        input_path_clusters_1_btn.setDisable(!input_path_clusters_1_btn.isDisable());
        input_path_clusters_2.setDisable(!input_path_clusters_2.isDisable());
        input_path_clusters_2_btn.setDisable(!input_path_clusters_2_btn.isDisable());
        output_dir.setDisable(!output_dir.isDisable());
        output_dir_btn.setDisable(!output_dir_btn.isDisable());
    }

    public void set_input_path_cluster_1() {
        File file = file_chooser.showOpenDialog(Main.primary_stage);
        if (file != null) {
            input_path_clusters_1.setText(".../"+file.getName());
            clusters_compare.set_input_path(file.getAbsolutePath());
        }
    }

    public void set_input_path_cluster_1(String dir_path, String name){
        clusters_compare.input_path = Paths.get(dir_path, name + ".txt").toString();
        input_path_clusters_1.setText(".../"+name+".txt");
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
        clusters_compare = new clusters_compare(area);
        init_config();
    }
}
