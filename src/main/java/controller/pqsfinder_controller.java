package controller;

import model.pqsfinder;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
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
public class pqsfinder_controller implements Initializable {
    private final FileChooser file_chooser = new FileChooser();
    private final DirectoryChooser dir_chooser = new DirectoryChooser();

    private Timeline createTimeline(int timeFreq, Runnable r) {
        Timeline tl = new Timeline(new KeyFrame(Duration.seconds(timeFreq), event -> r.run()));
        tl.setCycleCount(Timeline.INDEFINITE);
        return tl;
    }

    // pqs finder
    @FXML
    private TextField input_path;
    @FXML
    private TextField output_dir, output_name;
    @FXML
    private ChoiceBox<String> strand, overlapping;
    @FXML
    private TextField max_length, min_score, run_min_length, run_max_length;
    @FXML
    private TextField loop_min_length, loop_max_length, max_bulges, max_mismatches;
    @FXML
    private TextField max_defects;
    @FXML
    private TextArea area;
    @FXML
    private Button start_btn, stop_btn;

    Controller contr;

    public model.pqsfinder pqsfinder;
    private ExecutorService exec_service = Executors.newSingleThreadExecutor();
    private Timeline timeline = null;

    private void init() {

        strand.getItems().setAll("*", "+", "-");
        strand.setValue("*");
        strand.setTooltip(new Tooltip("Strand specification (+, - or *)."));

        overlapping.getItems().setAll("TRUE", "FALSE");
        overlapping.setValue("FALSE");
        overlapping.setTooltip(new Tooltip("If true, than overlapping PQS will be reported."));

        max_length.setTooltip(new Tooltip("Maximal total length of PQS."));

        min_score.setTooltip(new Tooltip("Minimal score of PQS to be reported."));

        run_min_length.setTooltip(new Tooltip("Minimal length of each PQS run (G-run)."));

        run_max_length.setTooltip(new Tooltip("Maximal length of each PQS run."));

        loop_min_length.setTooltip(new Tooltip("Minimal length of each PQS inner loop."));

        loop_max_length.setTooltip(new Tooltip("Maximal length of each PQS inner loop."));

        max_bulges.setTooltip(new Tooltip("Maximal number of runs containing a bulge."));

        max_mismatches.setTooltip(new Tooltip("Maximal number of runs containing a mismatch."));

        max_defects.setTooltip(new Tooltip("Maximum number of defects in total (#bulges + #mismatches)."));
    }

    public void start() {
        set_output_name();

        if (!pqsfinder.is_base_valid()) {
            pqsfinder.print_stream.println("values are NOT valid, something is missing!");
            //showAlert(Alert.AlertType.ERROR, cdhit_pnl.getScene().getWindow(), "Not valid!",
            //       "Values are NOT valid, something is missing!");
            return;
        }

        set_parameters();

        if (exec_service.isShutdown()) {
            exec_service = Executors.newSingleThreadExecutor();
        }
        start_btn.setDisable(true);
        stop_btn.setDisable(false);
        exec_service.execute(() -> {
            try {
                pqsfinder.start();
                exec_service.shutdownNow();
            } catch (Exception e) {
                pqsfinder.print_stream.println(e.toString());
                exec_service.shutdownNow();
                e.printStackTrace();
            }
        });
        java.util.Date date = new java.util.Date();
        pqsfinder.print_stream.println(date.toString() + " the process has started");
        Runnable pqsfinder_runnable = () -> {
            if (!exec_service.isShutdown()) {
                java.util.Date date1 = new java.util.Date();
                pqsfinder.print_stream.println(date1.toString() + " the process is running");
            } else {
                if (timeline != null) {
                    timeline.stop();
                    start_btn.setDisable(false);
                    stop_btn.setDisable(true);

                    contr.pqsareas_contr.set_input_path_pqs_positions(pqsfinder.output_path, pqsfinder.output_name);
                    contr.cdhit_contr.set_input_path_pqs(pqsfinder.output_path, pqsfinder.output_name);
                    contr.cdhit2D_contr.set_input_path_pqs_1(pqsfinder.output_path, pqsfinder.output_name);
                }
            }
        };
        timeline = createTimeline(5, pqsfinder_runnable);
        timeline.play();

    }

    public void stop() {
        if (pqsfinder.p.isAlive()) {
            pqsfinder.p.destroy();
        }
        java.util.Date date = new java.util.Date();
        pqsfinder.print_stream.println(date.toString() + " the process has been stopped externally");
        exec_service.shutdownNow();
        start_btn.setDisable(false);
        stop_btn.setDisable(true);
    }

    public void set_input_path() {
        File file = file_chooser.showOpenDialog(Main.primary_stage);
        if (file != null) {
            input_path.setText(".../"+file.getName());
            pqsfinder.set_input_path(file.getAbsolutePath());
            contr.pqsareas_contr.set_input_path_genome(file.getAbsolutePath(), file.getName());
        }
    }

    public void set_output_dir() throws IOException {
        File file = dir_chooser.showDialog(Main.primary_stage);
        if (file != null) {
            output_dir.setText(".../"+file.getName());
            pqsfinder.set_output_dir(file.getAbsolutePath());
            Main.config.output_dir_name = file.getName();
            Main.config.output_dir = file.getAbsolutePath();
            Main.save_config();
        }
    }

    private void set_parameters(){
        pqsfinder.setStrand(strand.getValue());
        pqsfinder.setOverlapping(overlapping.getValue());
        pqsfinder.setMax_len(max_length.getText());
        pqsfinder.setMin_score(min_score.getText());
        pqsfinder.setRun_min_len(run_min_length.getText());
        pqsfinder.setRun_max_len(run_max_length.getText());
        pqsfinder.setLoop_min_len(loop_min_length.getText());
        pqsfinder.setLoop_max_len(loop_max_length.getText());
        pqsfinder.setMax_bulges(max_bulges.getText());
        pqsfinder.setMax_mismatches(max_mismatches.getText());
        pqsfinder.setMax_defects(max_defects.getText());
    }

    private void set_output_name() {
        pqsfinder.set_output_name(output_name.getText());
    }

    public void init_config(){
        if (Main.config.output_dir_name != null && Main.config.output_dir != null) {
            output_dir.setText(".../"+Main.config.output_dir_name);
            pqsfinder.output_path = Main.config.output_dir;
        }
        //pqsfinder.set_program_path(Main.config.Rscript_path);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pqsfinder = new pqsfinder(area);
        pqsfinder.set_program_path(Main.jar_folder_path + "/g4.R");

        init();
        init_config();
    }
}
