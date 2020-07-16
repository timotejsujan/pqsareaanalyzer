package controller;

import javafx.scene.control.Button;
import model.pqsareas;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

/**
 * @author Timotej Sujan
 */
public class pqsareas_controller extends helper implements Initializable {

    public Button input_path_genom_btn;
    public Button input_path_pqs_positions_btn;
    public Button output_dir_btn;
    @FXML private TextField input_path_genom, input_path_pqs_positions;
    @FXML private TextField area_size;
    private pqsareas pqsareas;

    public void start() {
        pqsareas.set_area(Integer.parseInt(area_size.getText()));
        pqsareas.set_output_name(output_name.getText().replace(" ", "_"));
        output_name.setText(output_name.getText().replace(" ", "_"));

        if (!pqsareas.is_valid()) {
            pqsareas.print_stream.println("values are NOT valid, something is missing!");
            return;
        }
        exec_service = Executors.newSingleThreadExecutor();

        exec_service.execute(() -> {
            pqsareas.start();
            exec_service.shutdownNow();
        });
        switch_disabled();
        java.util.Date date = new java.util.Date();
        pqsareas.print_stream.println(date.toString() + " the process has started");
        Runnable pqsareas_runnable = () -> {
            if (!exec_service.isShutdown()) {
                java.util.Date date1 = new java.util.Date();
                pqsareas.print_stream.println(date1.toString() + " the process is running");
            } else {
                timeline.stop();
                switch_disabled();

                contr.cdhit_contr.set_input_path_areas(pqsareas.output_path, pqsareas.output_name);
                contr.cdhit2D_contr.set_input_path_areas_1(pqsareas.output_path, pqsareas.output_name);
            }
        };
        timeline = create_timeline(5, pqsareas_runnable);
        timeline.play();

    }

    public void stop() {
        java.util.Date date = new java.util.Date();
        pqsareas.print_stream.println(date.toString() + " the process has been stopped externally");
        exec_service.shutdownNow();
    }

    public void set_input_path_genome() {
        File file = file_chooser.showOpenDialog(Main.primary_stage);
        if (file != null) {
            input_path_genom.setText(".../"+file.getName());
            pqsareas.set_input_path(file.getAbsolutePath());
        }
    }

    public void set_input_path_genome(String path, String name){
        if (exec_service == null || exec_service.isShutdown()) {
            pqsareas.input_path = path;
            input_path_genom.setText(".../"+name);
        }
    }

    public void set_input_path_pqs_positions() {
        File file = file_chooser.showOpenDialog(Main.primary_stage);
        if (file != null) {
            input_path_pqs_positions.setText(".../"+file.getName());
            pqsareas.set_input_path_pqs_positions(file.getAbsolutePath());
        }
    }

    public void set_input_path_pqs_positions(String dir_path, String name){
        if (exec_service == null || exec_service.isShutdown()) {
            input_path_pqs_positions.setText(".../"+name+".txt");
            pqsareas.input_path_pqs_positions = dir_path + "/" + name + ".txt";
        }
    }

    public void set_output_dir() throws IOException {
        File file = dir_chooser.showDialog(Main.primary_stage);
        if (file != null) {
            output_dir.setText(".../"+file.getName());
            pqsareas.set_output_dir(file.getAbsolutePath());
            Main.config.output_dir_name = file.getName();
            Main.config.output_dir = file.getAbsolutePath();
            Main.save_config();
        }
    }

    private void switch_disabled(){
        start_btn.setDisable(!start_btn.isDisable());
        stop_btn.setDisable(!stop_btn.isDisable());
        input_path_genom.setDisable(!input_path_genom.isDisable());
        input_path_genom_btn.setDisable(!input_path_genom_btn.isDisable());
        input_path_pqs_positions.setDisable(!input_path_pqs_positions.isDisable());
        input_path_pqs_positions_btn.setDisable(!input_path_pqs_positions_btn.isDisable());
        output_dir.setDisable(!output_dir.isDisable());
        output_dir_btn.setDisable(!output_dir_btn.isDisable());
    }

    public void init_config(){
        if (exec_service != null && !exec_service.isShutdown()) return;

        if (Main.config.output_dir_name != null && Main.config.output_dir != null) {
            output_dir.setText(".../"+Main.config.output_dir_name);
            pqsareas.output_path = Main.config.output_dir;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pqsareas = new pqsareas(area);
        init_config();
    }
}
