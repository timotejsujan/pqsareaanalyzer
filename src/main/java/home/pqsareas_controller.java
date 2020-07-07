package home;

import external.pqsareas;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
public class pqsareas_controller implements Initializable {
    private final FileChooser file_chooser = new FileChooser();
    private final DirectoryChooser dir_chooser = new DirectoryChooser();

    private Timeline create_timeline(int time_freq, Runnable r) {
        Timeline tl = new Timeline(new KeyFrame(Duration.seconds(time_freq), event -> r.run()));
        tl.setCycleCount(Timeline.INDEFINITE);
        return tl;
    }

    @FXML
    private TextField input_path_genom, input_path_pqs_positions, output_name, output_dir;
    @FXML
    private TextField area_size;
    @FXML
    private Button start_btn, stop_btn;
    @FXML
    private TextArea area;
    private external.pqsareas pqsareas;
    private ExecutorService exec_service = Executors.newSingleThreadExecutor();
    private Timeline timeline = null;

    public void start() {
        set_output_name();
        set_area_size();
        if (!pqsareas.is_valid()) {
            pqsareas.print_stream.println("values are NOT valid, something is missing!");
            return;
        }
        if (exec_service.isShutdown()) {
            exec_service = Executors.newSingleThreadExecutor();
        }
        exec_service.execute(() -> {
            pqsareas.start();
            exec_service.shutdownNow();
        });
        start_btn.setDisable(true);
        stop_btn.setDisable(false);
        java.util.Date date = new java.util.Date();
        pqsareas.print_stream.println(date.toString() + " the process has started");
        Runnable pqsareas_runnable = () -> {
            if (!exec_service.isShutdown()) {
                java.util.Date date1 = new java.util.Date();
                pqsareas.print_stream.println(date1.toString() + " the process is running");
            } else {
                timeline.stop();
                start_btn.setDisable(false);
                stop_btn.setDisable(true);
            }
        };
        timeline = create_timeline(5, pqsareas_runnable);
        timeline.play();

    }

    public void stop() {
        java.util.Date date = new java.util.Date();
        pqsareas.print_stream.println(date.toString() + " the process has been stopped externally");
        exec_service.shutdownNow();
        start_btn.setDisable(false);
        stop_btn.setDisable(true);
    }

    public void set_input_path_genom() {
        File file = file_chooser.showOpenDialog(null);
        if (file != null) {
            input_path_genom.setText(file.getName());
            pqsareas.set_input_path(file.getAbsolutePath());
        }
    }

    public void set_input_path_pqs_positions() {
        File file = file_chooser.showOpenDialog(null);
        if (file != null) {
            input_path_pqs_positions.setText(file.getName());
            pqsareas.set_input_path_pqs_positions(file.getAbsolutePath());
        }
    }

    public void set_output_dir() {
        File file = dir_chooser.showDialog(null);
        if (file != null) {
            output_dir.setText(file.getName());
            pqsareas.set_output_dir(file.getAbsolutePath());
        }
    }

    public void set_output_name() {
        pqsareas.set_output_name(output_name.getText());
    }

    public void set_area_size() {
        pqsareas.set_area(Integer.parseInt(area_size.getText()));
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pqsareas = new pqsareas(area);

    }
}
