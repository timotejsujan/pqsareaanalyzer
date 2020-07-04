package home;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Timotej Sujan
 */
public class pqsfinder_controller {
    private final FileChooser fileChooser = new FileChooser();
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    private Timeline createTimeline(int timeFreq, Runnable r){
        Timeline tl =  new Timeline(new KeyFrame(Duration.seconds(timeFreq), event -> r.run()));
        tl.setCycleCount(Timeline.INDEFINITE);
        return tl;
    }

    // pqs finder
    @FXML
    private Pane pqsfinder_pnl, pqsfinder_infoPnl;
    @FXML
    private TextField pqsfinder_inputPath;
    @FXML
    private Button pqsfinder_btn, pqsfinder_info;
    @FXML
    private TextField pqsfinder_outputDir, pqsfinder_outputName;
    @FXML
    private ChoiceBox<String> pqsfinder_strand, pqsfinder_overlapping;
    @FXML
    private TextField pqsfinder_maxLen, pqsfinder_minScore, pqsfinder_runMinLen, pqsfinder_runMaxLen;
    @FXML
    private TextField pqsfinder_loopMinLen, pqsfinder_loopMaxLen, pqsfinder_maxBulges, pqsfinder_maxMismatches;
    @FXML
    private TextField pqsfinder_maxDefects;
    @FXML
    private TextArea pqsfinder_area;
    @FXML
    private Button pqsfinder_btnStart, pqsfinder_btnStop;
    @FXML
    private Text psqfinder_manual;
    private external.pqsfinder pqsfinder;
    private ExecutorService pqsfinder_execSer = Executors.newSingleThreadExecutor();
    private Timeline pqsfinder_timeline = null;

    private void pqsfinderInit(){
        pqsfinder_strand.getItems().setAll("", "*", "+", "-");
        pqsfinder_strand.setValue("*");
        pqsfinder_strand.setTooltip(new Tooltip("Strand specification (+, - or *)."));

        pqsfinder_overlapping.getItems().setAll("", "TRUE", "FALSE");
        pqsfinder_overlapping.setValue("FALSE");
        pqsfinder_overlapping.setTooltip(new Tooltip("If true, than overlapping PQS will be reported."));

        pqsfinder_maxLen.setTooltip(new Tooltip("Maximal total length of PQS."));

        pqsfinder_minScore.setTooltip(new Tooltip("Minimal score of PQS to be reported. " +
                "The default value 52 shows the best " +
                "balanced accuracy on human G4 " +
                "sequencing data (Chambers et al. 2015)."));

        pqsfinder_runMinLen.setTooltip(new Tooltip("Minimal length of each PQS run (G-run)."));

        pqsfinder_runMaxLen.setTooltip(new Tooltip("Maximal length of each PQS run."));

        pqsfinder_loopMinLen.setTooltip(new Tooltip("Minimal length of each PQS inner loop."));

        pqsfinder_loopMaxLen.setTooltip(new Tooltip("Maximal length of each PQS inner loop."));

        pqsfinder_maxBulges.setTooltip(new Tooltip("Maximal number of runs containing a bulge."));

        pqsfinder_maxMismatches.setTooltip(new Tooltip("Maximal number of runs containing a mismatch."));

        pqsfinder_maxDefects.setTooltip(new Tooltip("Maximum number of defects in total (#bulges + #mismatches)."));

        psqfinder_manual.setText(
                "Pqsfinder is a program for detecting DNA sequence patterns\nthat are likely to fold into an intramolecular G-quadruplex.\n\n" +
                        "After setting the input path to a genom and setting parameters,\nyou will get a file of positions of quadruplexes,\n" +
                        "which you can use in another programs in this application.\n\n" +
                        "Program will run from several minutes to a few hours depending on a size and structure of the genom.\n\n" +
                        "If you hover over the parameter's field, you will get additional information for the concrete parameter.\n\n" +
                        "You can read complete documentation after pressing the button \"Doc\"\nor in the folder \"Documentation\" in the root of the application.");
    }

    public void pqsfinderStart() {
        pqsfinderSetOutputName();
        if (!pqsfinder.is_base_valid()) {
            pqsfinder.print_stream.println("values are NOT valid, something is missing!");
            //showAlert(Alert.AlertType.ERROR, cdhit_pnl.getScene().getWindow(), "Not valid!",
             //       "Values are NOT valid, something is missing!");
            return;
        }
        pqsfinderSetStrand();
        pqsfinderSetOverlapping();
        pqsfinderSetMax_len();
        pqsfinderSetMin_score();
        pqsfinderSetRun_min_len();
        pqsfinderSetRun_max_len();
        pqsfinderSetLoop_min_len();
        pqsfinderSetLoop_max_len();
        pqsfinderSetMax_bulges();
        pqsfinderSetMax_mismatches();
        pqsfinderSetMax_defects();

        if (pqsfinder_execSer.isShutdown()){
            pqsfinder_execSer = Executors.newSingleThreadExecutor();
        }
        pqsfinder_btnStart.setDisable(true);
        pqsfinder_btnStop.setDisable(false);
        pqsfinder_execSer.execute(() -> {
            try {
                pqsfinder.start();
                pqsfinder_execSer.shutdownNow();
            } catch (Exception e) {
                pqsfinder.print_stream.println(e.toString());
                pqsfinder_execSer.shutdownNow();
                e.printStackTrace();
            }
        });
        java.util.Date date = new java.util.Date();
        pqsfinder.print_stream.println(date.toString() + " the process has started");
        Runnable pqsfinder_runnable = () -> {
            if(!pqsfinder_execSer.isShutdown()){
                java.util.Date date1 = new java.util.Date();
                pqsfinder.print_stream.println(date1.toString() + " the process is running");
            } else {
                if(pqsfinder_timeline != null) {
                    pqsfinder_timeline.stop();
                    pqsfinder_btnStart.setDisable(false);
                    pqsfinder_btnStop.setDisable(true);
                }
            }
        };
        pqsfinder_timeline = createTimeline(5, pqsfinder_runnable);
        pqsfinder_timeline.play();

    }

    public void pqsfinderStop(){
        if (pqsfinder.p.isAlive()) {
            pqsfinder.p.destroy();
        }
        java.util.Date date = new java.util.Date();
        pqsfinder.print_stream.println(date.toString() + " the process has been stopped externally");
        pqsfinder_execSer.shutdownNow();
        pqsfinder_btnStart.setDisable(false);
        pqsfinder_btnStop.setDisable(true);
    }

    public void pqsfinderSetInputPath() {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            pqsfinder_inputPath.setText(file.getName());
            pqsfinder.set_input_path(file.getAbsolutePath());
        }
    }

    public void pqsfinderSetOutputDir() {
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            pqsfinder_outputDir.setText(file.getName());
            pqsfinder.set_output_path(file.getAbsolutePath());
        }
    }

    public void pqsfinderOpenDoc() throws IOException {
        Desktop desktop = Desktop.getDesktop();
        File f = new File(Main.jar_folder_path+"/documentation/pqsfinder.pdf");
        desktop.open(f);
    }

    private void pqsfinderSetOutputName() {
        pqsfinder.set_output_name(pqsfinder_outputName.getText());
    }

    private void pqsfinderSetStrand() {
        pqsfinder.setStrand(pqsfinder_strand.getValue());
    }

    private void pqsfinderSetOverlapping() {
        pqsfinder.setOverlapping(pqsfinder_overlapping.getValue());
    }

    private void pqsfinderSetMax_len() {
        pqsfinder.setMax_len(pqsfinder_maxLen.getText());
    }

    private void pqsfinderSetMin_score() {
        pqsfinder.setMin_score(pqsfinder_minScore.getText());
    }

    private void pqsfinderSetRun_min_len() {
        pqsfinder.setRun_min_len(pqsfinder_runMinLen.getText());
    }

    private void pqsfinderSetRun_max_len() {
        pqsfinder.setRun_max_len(pqsfinder_runMaxLen.getText());
    }

    private void pqsfinderSetLoop_min_len() {
        pqsfinder.setLoop_min_len(pqsfinder_loopMinLen.getText());
    }

    private void pqsfinderSetLoop_max_len() {
        pqsfinder.setLoop_max_len(pqsfinder_loopMaxLen.getText());
    }

    private void pqsfinderSetMax_bulges() {
        pqsfinder.setMax_bulges(pqsfinder_maxBulges.getText());
    }

    private void pqsfinderSetMax_mismatches() {
        pqsfinder.setMax_mismatches(pqsfinder_maxMismatches.getText());
    }

    private void pqsfinderSetMax_defects() {
        pqsfinder.setMax_defects(pqsfinder_maxDefects.getText());
    }
}
