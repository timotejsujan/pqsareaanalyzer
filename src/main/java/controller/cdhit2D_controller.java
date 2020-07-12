package controller;

import model.cdhit2D;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Timotej Sujan
 */
public class cdhit2D_controller implements Initializable {
    private final FileChooser file_chooser = new FileChooser();
    private final DirectoryChooser dir_chooser = new DirectoryChooser();

    private Timeline create_timeline(int time_freq, Runnable r) {
        Timeline tl = new Timeline(new KeyFrame(Duration.seconds(time_freq), event -> r.run()));
        tl.setCycleCount(Timeline.INDEFINITE);
        return tl;
    }

    @FXML
    private TextField input_path_areas_1, input_path_areas_2, output_name, output_dir, params;
    @FXML
    private TextField input_path_pqs_1, input_path_pqs_2;
    @FXML
    private TextArea area;
    public cdhit2D cdhit2D;
    private Timeline timeline;
    private ExecutorService exec_service;
    @FXML
    private Button start_btn, stop_btn;
    @FXML
    private CheckBox keep_one_sized;

    base_controller contr;

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
        Runnable r = () -> {
            if (exec_service.isShutdown()) {
                start_btn.setDisable(false);
                stop_btn.setDisable(true);
                timeline.stop();

                try {
                    contr.clusters_logo_contr.set_input_path(cdhit2D.output_path, cdhit2D.output_name);
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
            input_path_areas_1.setText(".../"+name);
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
            input_path_pqs_1.setText(".../"+name);
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
        if (Main.config.output_dir_name != null && Main.config.output_dir != null) {
            output_dir.setText(".../"+Main.config.output_dir_name);
            cdhit2D.output_path = Main.config.output_dir;
        }
        cdhit2D.set_program_path(Main.config.cdhit2D_path);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /*
        String cdhit_path = "";
        if (Main.isWindows()) cdhit_path = "cdhit-windows";
        if (Main.isUnix()) cdhit_path = "cdhit-linux";
        if (Main.isMac()) cdhit_path = "cdhit-osx";
*/
        cdhit2D = new cdhit2D(area);
        //cdhit2D.set_program_path(Main.jar_folder_path + "/" + cdhit_path + "/cd-hit-est-2d");

        init_config();
    }
}
