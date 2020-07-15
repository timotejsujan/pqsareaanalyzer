package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.util.concurrent.ExecutorService;

/**
 * @author Timotej Sujan
 */
public class helper {

    @FXML
    TextField output_dir, output_name;
    @FXML
    Button start_btn, stop_btn;
    @FXML
    TextArea area;

    base_controller contr;

    protected ExecutorService exec_service;
    protected Timeline timeline;

    protected final FileChooser file_chooser = new FileChooser();
    protected final DirectoryChooser dir_chooser = new DirectoryChooser();
    protected Timeline create_timeline(int time_freq, Runnable r) {
        Timeline tl = new Timeline(new KeyFrame(Duration.seconds(time_freq), event -> r.run()));
        tl.setCycleCount(Timeline.INDEFINITE);
        return tl;
    }
}
