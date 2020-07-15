package controller;

import model.cdhit2D;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

/**
 * @author Timotej Sujan
 */
public class cdhit2D_controller extends helper implements Initializable {
    @FXML private TextField input_path_areas_1, input_path_areas_2, params;
    @FXML private TextField input_path_pqs_1, input_path_pqs_2;
    @FXML private CheckBox keep_one_sized;

    public cdhit2D cdhit2D;

    public void start() {
        cdhit2D.set_keep_one_sized(keep_one_sized.isSelected());
        cdhit2D.set_output_name(output_name.getText());
        cdhit2D.set_params(params.getText());

        if (!cdhit2D.is_base_valid() || !cdhit2D.is_valid()) {
            cdhit2D.print_stream.println("values are NOT valid, something is missing!");
            return;
        }
        exec_service = Executors.newSingleThreadExecutor();

        exec_service.execute(() -> {
            try {
                cdhit2D.start();
                exec_service.shutdownNow();
            } catch (Exception e) {
                cdhit2D.print_stream.println(e.toString());
                exec_service.shutdownNow();
                e.printStackTrace();
            }
        });
        start_btn.setDisable(true);
        stop_btn.setDisable(false);
        java.util.Date date = new java.util.Date();
        cdhit2D.print_stream.println(date.toString() + " the process has started");
        Runnable r = () -> {
            if (!exec_service.isShutdown()){
                java.util.Date date1 = new java.util.Date();
                cdhit2D.print_stream.println(date1.toString() + " the process is running");
            } else{
                start_btn.setDisable(false);
                stop_btn.setDisable(true);
                timeline.stop();

                try {
                    contr.cluster_logo_contr.set_input_path(cdhit2D.output_path, cdhit2D.output_name);
                    contr.clusters_compare_contr.set_input_path_cluster_1(cdhit2D.output_path, cdhit2D.output_name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        timeline = create_timeline(5, r);
        timeline.play();
    }

    public void stop() {
        if (cdhit2D.p.isAlive()) {
            cdhit2D.p.destroy();
        }
        exec_service.shutdownNow();
        start_btn.setDisable(false);
        stop_btn.setDisable(true);
        java.util.Date date = new java.util.Date();
        cdhit2D.print_stream.println(date.toString() + " the process has been stopped externally");
    }

    public void set_input_path_areas_1() {
        File file = file_chooser.showOpenDialog(Main.primary_stage);
        if (file != null) {
            input_path_areas_1.setText(".../"+file.getName());
            cdhit2D.set_input_path(file.getAbsolutePath());
        }
    }

    public void set_input_path_areas_1(String dir_path, String name) {
        if (exec_service == null || exec_service.isShutdown()) {
            input_path_areas_1.setText(".../"+name+".txt");
            cdhit2D.input_path = dir_path + "/" + name + ".txt";
        }
    }

    public void set_input_path_areas_2() {
        File file = file_chooser.showOpenDialog(Main.primary_stage);
        if (file != null) {
            input_path_areas_2.setText(".../"+file.getName());
            cdhit2D.set_input_path_areas_2(file.getAbsolutePath());
        }
    }

    public void set_input_path_pqs_1() {
        File file = file_chooser.showOpenDialog(Main.primary_stage);
        if (file != null) {
            input_path_pqs_1.setText(".../"+file.getName());
            cdhit2D.set_input_path_pqs_1(file.getAbsolutePath());
        }
    }

    public void set_input_path_pqs_1(String dir_path, String name) {
        if (exec_service == null || exec_service.isShutdown()) {
            input_path_pqs_1.setText(".../"+name+".txt");
            cdhit2D.input_path_pqs_1 = dir_path + "/" + name + ".txt";
        }
    }

    public void set_input_path_pqs_2() {
        File file = file_chooser.showOpenDialog(Main.primary_stage);
        if (file != null) {
            input_path_pqs_2.setText(".../"+file.getName());
            cdhit2D.set_input_path_pqs_2(file.getAbsolutePath());
        }
    }

    public void set_output_dir() throws IOException {
        File file = dir_chooser.showDialog(Main.primary_stage);
        if (file != null) {
            output_dir.setText(".../"+file.getName());
            cdhit2D.set_output_dir(file.getAbsolutePath());
            Main.config.output_dir_name = file.getName();
            Main.config.output_dir = file.getAbsolutePath();
            Main.save_config();
        }
    }

    public void init_config(){
        if (exec_service != null && !exec_service.isShutdown()) return;

        if (Main.config.output_dir_name != null && Main.config.output_dir != null) {
            output_dir.setText(".../"+Main.config.output_dir_name);
            cdhit2D.output_path = Main.config.output_dir;
        }
        if (Main.config.cdhit2D_path != null)
            cdhit2D.set_program_path(Main.config.cdhit2D_path);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cdhit2D = new cdhit2D(area);
        init_config();
    }
}
