package controller;

import javafx.scene.control.Button;
import model.cdhit;
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
public class cdhit_controller extends helper implements Initializable {
    public Button output_dir_btn;
    public Button input_path_areas_btn;
    public Button input_path_pqs_btn;
    @FXML private TextField input_path_areas, params, input_path_pqs;
    @FXML public CheckBox keep_one_sized;

    public cdhit cdhit;

    public void start() {
        cdhit.set_keep_one_sized(keep_one_sized.isSelected());
        cdhit.set_output_name(output_name.getText());
        cdhit.set_params(params.getText());

        if (!cdhit.is_base_valid() || !cdhit.is_valid()) {
            cdhit.print_stream.println("values are NOT valid, something is missing!");
            return;
        }
        exec_service = Executors.newSingleThreadExecutor();

        exec_service.execute(() -> {
            try {
                cdhit.start();
                exec_service.shutdownNow();
            } catch (Exception e) {
                cdhit.print_stream.println(e.toString());
                exec_service.shutdownNow();
                e.printStackTrace();
            }
        });
        switch_disabled();
        java.util.Date date = new java.util.Date();
        cdhit.print_stream.println(date.toString() + " the process has started");
        Runnable r = () -> {
            if (!exec_service.isShutdown()) {
                java.util.Date date1 = new java.util.Date();
                cdhit.print_stream.println(date1.toString() + " the process is running");
            }
            else {
                switch_disabled();
                timeline.stop();

                try {
                    contr.cluster_logo_contr.set_input_path(cdhit.output_path, cdhit.output_name);
                    contr.clusters_compare_contr.set_input_path_cluster_1(cdhit.output_path, cdhit.output_name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        };
        timeline = create_timeline(5, r);
        timeline.play();
    }

    public void stop() {
        exec_service.shutdownNow();
        java.util.Date date = new java.util.Date();
        cdhit.print_stream.println(date.toString() + " the process has been stopped externally");
    }

    public void set_input_path_areas() {
        File file = file_chooser.showOpenDialog(Main.primary_stage);
        if (file != null) {
            input_path_areas.setText(".../"+file.getName());
            cdhit.set_input_path(file.getAbsolutePath());
        }
    }

    public void set_input_path_areas(String dir_path, String name) {
        if (exec_service == null || exec_service.isShutdown()) {
            input_path_areas.setText(".../"+name+".txt");
            cdhit.input_path = dir_path + "/" + name + ".txt";
        }
    }

    public void set_input_path_pqs() {
        File file = file_chooser.showOpenDialog(Main.primary_stage);
        if (file != null) {
            input_path_pqs.setText(".../"+file.getName());
            cdhit.set_input_path_pqs(file.getPath());
        }
    }

    public void set_input_path_pqs(String dir_path, String name) {
        if (exec_service == null || exec_service.isShutdown()) {
            input_path_pqs.setText(".../"+name+".txt");
            cdhit.input_path_pqs = dir_path + "/" + name + ".txt";
        }
    }

    public void set_output_dir() throws IOException {
        File file = dir_chooser.showDialog(Main.primary_stage);
        if (file != null) {
            output_dir.setText(".../"+file.getName());
            cdhit.set_output_dir(file.getAbsolutePath());
            Main.config.output_dir_name = file.getName();
            Main.config.output_dir = file.getAbsolutePath();
            Main.save_config();
        }
    }

    private void switch_disabled(){
        start_btn.setDisable(!start_btn.isDisable());
        stop_btn.setDisable(!stop_btn.isDisable());
        input_path_pqs.setDisable(!input_path_pqs.isDisable());
        input_path_pqs_btn.setDisable(!input_path_pqs_btn.isDisable());
        input_path_areas.setDisable(!input_path_areas.isDisable());
        input_path_areas_btn.setDisable(!input_path_areas_btn.isDisable());
        output_dir.setDisable(!output_dir.isDisable());
        output_dir_btn.setDisable(!output_dir_btn.isDisable());
    }

    public void init_config(){
        if (exec_service != null && !exec_service.isShutdown()) return;

        if (Main.config.output_dir_name != null && Main.config.output_dir != null) {
            output_dir.setText(".../"+Main.config.output_dir_name);
            cdhit.output_path = Main.config.output_dir;
        }
        if (Main.config.cdhit_path != null)
            cdhit.set_program_path(Main.config.cdhit_path);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cdhit = new cdhit(area);
        init_config();
    }
}
