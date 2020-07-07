package home;

import external.cdhit;
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
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Timotej Sujan
 */
public class cdhit_controller implements Initializable {
    private final FileChooser file_chooser = new FileChooser();
    private final DirectoryChooser dir_chooser = new DirectoryChooser();

    private Timeline create_timeline(int time_freq, Runnable r) {
        Timeline tl = new Timeline(new KeyFrame(Duration.seconds(time_freq), event -> r.run()));
        tl.setCycleCount(Timeline.INDEFINITE);
        return tl;
    }

    @FXML
    private Button start_btn, stop_btn;
    @FXML
    private TextField input_path_areas, output_name, output_dir, params, input_path_pqs;
    @FXML
    private TextArea area;
    private external.cdhit cdhit;
    private ExecutorService exec_service = Executors.newSingleThreadExecutor();
    private Timeline timeline;
    @FXML
    public CheckBox keep_one_sized;

    public void start() {
        set_keep_one_sized();
        set_output_name();
        if (!cdhit.is_base_valid() || !cdhit.is_valid()) {
            cdhit.print_stream.println("values are NOT valid, something is missing!");
            return;
        }
        set_params();
        if (exec_service.isShutdown()) {
            exec_service = Executors.newSingleThreadExecutor();
        }
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
        start_btn.setDisable(true);
        stop_btn.setDisable(false);
        Runnable r = () -> {
            if (exec_service.isShutdown()) {
                start_btn.setDisable(false);
                stop_btn.setDisable(true);
                timeline.stop();
            }
        };
        timeline = create_timeline(5, r);
        timeline.play();
    }

    public void stop() {
        if (cdhit.p.isAlive()) {
            cdhit.p.destroy();
        }
        exec_service.shutdownNow();
        start_btn.setDisable(false);
        stop_btn.setDisable(true);
        java.util.Date date = new java.util.Date();
        cdhit.print_stream.println(date.toString() + " the process has been stopped externally");
    }

    public void set_input_path_areas() {
        File file = file_chooser.showOpenDialog(null);
        if (file != null) {
            input_path_areas.setText(file.getName());
            cdhit.set_input_path(file.getAbsolutePath());
            //parameters_cdhit.appendText(" -i "+file.getPath()+" ");
        }
    }

    private void set_output_name() {
        cdhit.set_output_name(output_name.getText());
    }

    private void set_params() {
        cdhit.set_params(params.getText());
    }

    public void set_input_path_pqs() {
        File file = file_chooser.showOpenDialog(null);
        if (file != null) {
            input_path_pqs.setText(file.getName());
            cdhit.set_input_path_pqs(file.getPath());
        }
    }

    public void set_output_dir() {
        File file = dir_chooser.showDialog(null);
        if (file != null) {
            output_dir.setText(file.getName());
            cdhit.set_output_dir(file.getAbsolutePath());
        }
    }

    public void set_keep_one_sized() {
        cdhit.set_keep_one_sized(keep_one_sized.isSelected());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String cdhit_path = "";
        if (Main.isWindows()) cdhit_path = "cdhit-windows";
        if (Main.isUnix()) cdhit_path = "cdhit-linux";
        if (Main.isMac()) cdhit_path = "cdhit-osx";

        cdhit = new cdhit(area);
        cdhit.set_program_path(Main.jar_folder_path + "/" + cdhit_path + "/cd-hit-est");
    }
}
