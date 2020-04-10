package home;

import external.CDhit;
import external.CDhit2;
import external.PQSfinder;

import internal.BlastApi;
import internal.ClustersCount;
import internal.PQSareas;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller extends Application implements Initializable {

    private String background_color = "gray";

    @FXML
    private VBox pnItems = null;

    // pqs finder
    @FXML
    private Pane pqsfinder_pnl;
    @FXML
    private Pane pqsfinder_infoPnl;
    @FXML
    private TextField pqsfinder_inputPath;
    @FXML
    private Button pqsfinder_btn;
    @FXML
    private Button pqsfinder_info;
    @FXML
    private TextField pqsfinder_outputDir;
    @FXML
    private TextField pqsfinder_outputName;
    @FXML
    private ChoiceBox<String> pqsfinder_strand;
    @FXML
    private ChoiceBox<String> pqsfinder_overlapping;
    @FXML
    private TextField pqsfinder_maxLen;
    @FXML
    private TextField pqsfinder_minScore;
    @FXML
    private TextField pqsfinder_runMinLen;
    @FXML
    private TextField pqsfinder_runMaxLen;
    @FXML
    private TextField pqsfinder_loopMinLen;
    @FXML
    private TextField pqsfinder_loopMaxLen;
    @FXML
    private TextField pqsfinder_maxBulges;
    @FXML
    private TextField pqsfinder_maxMismatches;
    @FXML
    private TextField pqsfinder_maxDefects;
    @FXML
    private TextArea pqsfinder_area;
    @FXML
    private Button pqsfinder_btnStart;
    @FXML
    private Text psqfinder_manual;
    @FXML
    private Button pqsfinder_btnStop;
    private PQSfinder pqsfinder;
    private ExecutorService pqsfinder_execSer = Executors.newSingleThreadExecutor();
    private Tooltip tt_strand = new Tooltip();
    private Tooltip tt_overlapping = new Tooltip();
    private Tooltip tt_max_len = new Tooltip();
    private Tooltip tt_min_score = new Tooltip();
    private Tooltip tt_run_min_len = new Tooltip();
    private Tooltip tt_run_max_len = new Tooltip();
    private Tooltip tt_loop_min_len = new Tooltip();
    private Tooltip tt_loop_max_len = new Tooltip();
    private Tooltip tt_max_bulges = new Tooltip();
    private Tooltip tt_max_mismatches = new Tooltip();
    private Tooltip tt_max_defects = new Tooltip();
    private Timeline pqsfinder_timeline = null;

    private void pqsfinderInit(){
        pqsfinder_strand.getItems().setAll("", "*", "+", "-");
        pqsfinder_strand.setValue("*");
        tt_strand.setText("Strand specification (+, - or *).");
        pqsfinder_strand.setTooltip(tt_strand);

        pqsfinder_overlapping.getItems().setAll("", "TRUE", "FALSE");
        pqsfinder_overlapping.setValue("FALSE");
        tt_overlapping.setText("If true, than overlapping PQS will be reported.");
        pqsfinder_overlapping.setTooltip(tt_overlapping);

        tt_max_len.setText("Maximal total length of PQS.");
        pqsfinder_maxLen.setTooltip(tt_max_len);

        tt_min_score.setText("Minimal score of PQS to be reported. The default value 52 shows the best balanced accuracy on human G4 sequencing data (Chambers et al. 2015).");
        pqsfinder_minScore.setTooltip(tt_min_score);

        tt_run_min_len.setText("Minimal length of each PQS run (G-run).");
        pqsfinder_runMinLen.setTooltip(tt_run_min_len);

        tt_run_max_len.setText("Maximal length of each PQS run.");
        pqsfinder_runMaxLen.setTooltip(tt_run_max_len);

        tt_loop_min_len.setText("Minimal length of each PQS inner loop.");
        pqsfinder_loopMinLen.setTooltip(tt_loop_min_len);

        tt_loop_max_len.setText("Maximal length of each PQS inner loop.");
        pqsfinder_loopMaxLen.setTooltip(tt_loop_max_len);

        tt_max_bulges.setText("Maximal number of runs containing a bulge.");
        pqsfinder_maxBulges.setTooltip(tt_max_bulges);

        tt_max_mismatches.setText("Maximal number of runs containing a mismatch.");
        pqsfinder_maxMismatches.setTooltip(tt_max_mismatches);

        tt_max_defects.setText("Maximum number of defects in total (#bulges + #mismatches).");
        pqsfinder_maxDefects.setTooltip(tt_max_defects);

        psqfinder_manual.setText(
                "Pqsfinder is a program for detecting DNA sequence patterns\nthat are likely to fold into an intramolecular G-quadruplex.\n\n" +
                "After setting the input path to a genom and setting parameters,\nyou will get a file of positions of quadruplexes,\n" +
                "which you can use in another programs in this application.\n\n" +
                "Program will run from several minutes to a few hours depending on a size and structure of the genom.\n\n" +
                "If you hover over the parameter's field, you will get additional information for the concrete parameter.\n\n" +
                "You can read complete documentation after pressing the button \"Doc\"\nor in the folder \"Documentation\" in the root of the application.");
    }

    public void pqsfinderStart() throws Exception {
        pqsfinderSetOutputName();
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
        //pqsfinder_thread

        /*
        pqsfinder_thread = new Thread(() -> {
            try {
                pqsfinder.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });*/
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
                e.printStackTrace();
            }
        });
        //pqsfinder_thread.start();
        java.util.Date date = new java.util.Date();
        pqsfinder.printStream.println(date.toString() + " the process has started");
        Runnable pqsfinder_runnable = () -> {
            if(!pqsfinder_execSer.isShutdown()){
                java.util.Date date1 = new java.util.Date();
                pqsfinder.printStream.println(date1.toString() + " the process is running");
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
        pqsfinder.printStream.println(date.toString() + " the process has been stopped externally");
        pqsfinder_execSer.shutdownNow();
        pqsfinder_btnStart.setDisable(false);
        pqsfinder_btnStop.setDisable(true);
    }

    public void pqsfinderSetInputPath() {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            pqsfinder_inputPath.setText(file.getName());
            pqsfinder.setInputPath(file.getAbsolutePath());
        }
    }

    public void pqsfinderSetOutputDir() {
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            pqsfinder_outputDir.setText(file.getName());
            pqsfinder.setOutputDir(file.getAbsolutePath());
        }
    }

    public void pqsfinderOpenDoc() throws IOException {
        Desktop desktop = Desktop.getDesktop();
        File f = new File("documentation/pqsfinder.pdf");
        desktop.open(f);
    }

    private void pqsfinderSetOutputName() {
        pqsfinder.setOutputName(pqsfinder_outputName.getText());
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


    // CD hit
    @FXML
    private Pane cdhit_pnl;
    @FXML
    private Button cdhit_btn;
    @FXML
    private Button cdhit_btnStart;
    @FXML
    private Button cdhit_btnStop;
    @FXML
    private Pane cdhit_infoPnl;
    @FXML
    private Button cdhit_info;
    @FXML
    private TextField cdhit_inputPath;
    @FXML
    private TextField cdhit_outputDir;
    @FXML
    private TextField cdhit_outputName;
    @FXML
    private TextField cdhit_params;
    @FXML
    private TextArea cdhit_area;
    private CDhit cdhit;
    private ExecutorService cdhit_execSer = Executors.newSingleThreadExecutor();
    private Timeline cdhit_timeline;
    @FXML
    private Text cdhit_manual;
    @FXML
    private TextField input_path_pqs;
    public void cdhitStart() {
        cdhitSetOutputDir();
        cdhitSetParams();
        if (cdhit_execSer.isShutdown()) {
            cdhit_execSer = Executors.newSingleThreadExecutor();
        }
        cdhit_execSer.execute(() -> {
            try {
                cdhit.start();
                cdhit_execSer.shutdownNow();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        cdhit_btnStart.setDisable(true);
        cdhit_btnStop.setDisable(false);
        Runnable r = () -> {
            if (cdhit_execSer.isShutdown()){
                cdhit_btnStart.setDisable(false);
                cdhit_btnStop.setDisable(true);
                cdhit_timeline.stop();
            }
        };
        cdhit_timeline = createTimeline(5, r);
        cdhit_timeline.play();
    }

    public void cdhitStop(){
        if (cdhit.p.isAlive()){
            cdhit.p.destroy();
        }
        cdhit_execSer.shutdownNow();
        cdhit_btnStart.setDisable(false);
        cdhit_btnStop.setDisable(true);
        java.util.Date date = new java.util.Date();
        cdhit.printStream.println(date.toString() + " the process has been stopped externally");
    }

    public void cdhitSetInputPath(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            cdhit_inputPath.setText(file.getName());
            cdhit.setInputPath(file.getAbsolutePath());
            //parameters_cdhit.appendText(" -i "+file.getPath()+" ");
        }
    }

    private void cdhitSetOutputDir() {
        cdhit.setOutputName(cdhit_outputDir.getText());
    }

    private void cdhitSetParams() {
        cdhit.setParameters(cdhit_params.getText());
    }

    public void cdhitSetInputPQS(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            input_path_pqs.setText(file.getName());
            cdhit.setInputPathPqs(file.getPath());
        }
    }

    public void cdhitSetOutputName(ActionEvent actionEvent) {
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            cdhit_outputName.setText(file.getName());
            cdhit.setOutputPath(file.getAbsolutePath());
        }
    }

    public void cdhitOpenDoc() throws IOException {
        Desktop desktop = Desktop.getDesktop();
        File f = new File("documentation/cdhit.pdf");
        desktop.open(f);
    }

    public void cdhitInit(){
        cdhit_manual.setText(
                "CD-HIT-EST clusters a nucleotide dataset into clusters that meet\na user-defined similarity threshold, " +
                "usually a sequence identity.\nThe input is a DNA/RNA dataset in fasta format and the output is\n" +
                "a fasta file of sequences in a list of sorted clusters.\n" +
                "Since eukaryotic genes usually have long introns, which cause long gaps, it is difficult to make " +
                "\nfull-length alignments for these genes. \n" +
                "So, CD-HIT-EST is good for non-intron containing sequences like EST. \n\n"+
                "Program will run from several seconds to a few hours depending on \n" +
                "a size and structure of the input file and given parameters.\n\n" +
                "You can read complete documentation after pressing the button \"Doc\"\nor in the folder \"Documentation\" in the root of the application.");
    }

    // CD hit 2
    @FXML
    private Pane cdhit2_pnl;
    @FXML
    private Button cdhit2_btn;
    @FXML
    private Pane cdhit2_infoPnl;
    @FXML
    private Button cdhit2_info;
    @FXML
    private TextField cdhit2_inputPath;
    @FXML
    private TextField cdhit2_inputPath2;
    @FXML
    private TextField cdhit2_outputName;
    @FXML
    private TextField cdhit2_outputDir;
    @FXML
    private TextField cdhit2_params;
    @FXML
    private TextArea cdhit2_area;
    private CDhit2 cdhit2;
    private Thread cdhit2_thread;
    @FXML
    private TextField cdhit2_InputPathPQS;
    @FXML
    private Text cdhit2_manual;
    public void cdhit2Start(ActionEvent actionEvent) {
        cdhit2SetOutputName();
        cdhit2SetParams();
        cdhit2_thread = new Thread(() -> {
            try {
                cdhit2.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        cdhit2_thread.start();

    }

    public void cdhit2Stop(ActionEvent actionEvent){
        cdhit2_thread.interrupt();
    }

    public void cdhit2SetInputPath(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            cdhit2_inputPath.setText(file.getName());
            cdhit2.setInputPath(file.getAbsolutePath());
        }
    }

    public void cdhit2SetInputPath2(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            cdhit2_inputPath2.setText(file.getName());
            cdhit2.setInputPath2(file.getAbsolutePath());
        }
    }

    public void cdhit2SetOutputName() {
        cdhit2.setOutputName(cdhit2_outputName.getText());
    }

    public void cdhit2SetParams() {
        cdhit2.setParams(cdhit2_params.getText());
    }

    public void cdhit2SetInputPQS(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            cdhit2_InputPathPQS.setText(file.getName());
            cdhit2.setInputPathPqs(file.getAbsolutePath());
        }
    }

    public void cdhit2SetOutputDir(ActionEvent actionEvent) {
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            cdhit2_outputDir.setText(file.getName());
            cdhit2.setOutputPath(file.getAbsolutePath());
        }
    }

    public void cdhit2OpenDoc() throws IOException {
        cdhitOpenDoc();
    }

    public void cdhit2Init(){
        cdhit2_manual.setText(
            "CD-HIT-EST-2D compares 2 nucleotide datasets (db1, db2).\n" +
            "It identifies the sequences in db2 that are similar to db1\n" +
            "at a certain threshold. The input are two DNA/RNA datasets (db1, db2)\n" +
            "in fasta format and the output are two files: a fasta file of sequences\n" +
            "in db2 that are not similar to db1 and a text file that lists similar\n" +
            "sequences between db1 & db2. For same reason as CD-HIT-EST, CD-HIT-EST-2D\n" +
            "is good for non-intron containing sequences like EST.\n\n" +
            "Program will run from several seconds to a few hours depending on \n" +
            "a size and structure of the input file and given parameters.\n\n" +
            "You can read complete documentation after pressing the button \"Doc\"\nor in the folder \"Documentation\" in the root of the application.");
    }


    // pqs areas
    @FXML
    private Pane pqsareas_pnl;
    @FXML
    private Pane pqsareas_infoPnl;
    @FXML
    private TextField input1_path_pqsareas;
    @FXML
    private TextField input2_path_pqsareas;
    @FXML
    private TextField pqsareas_outputName;
    @FXML
    private TextField pqsareas_outputDir;
    @FXML
    private Button pqsareas_btn;
    @FXML
    private Button pqsareas_btnStart;
    @FXML
    private Button pqsareas_btnStop;
    @FXML
    private Button pqsareas_info;
    @FXML
    private TextField pqsareas_areaSize;
    @FXML
    private Text psqareas_manual;
    @FXML
    private TextArea pqsareas_area;
    private PQSareas pqsareas;
    private ExecutorService pqsareas_execSer = Executors.newSingleThreadExecutor();
    private Timeline pqsareas_timeline = null;

    public void pqsareasStart() {
        pqsareasSetAreaSize();
        pqsareasSetOutputName();
        pqsareasSetOutputName();
        if (pqsareas_execSer.isShutdown()) {
            pqsareas_execSer = Executors.newSingleThreadExecutor();
        }
        pqsareas_execSer.execute(() -> {
            try {
                pqsareas.start();
                pqsareas_execSer.shutdownNow();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        pqsareas_btnStart.setDisable(true);
        pqsareas_btnStop.setDisable(false);
        java.util.Date date = new java.util.Date();
        pqsareas.printStream.println(date.toString() + " the process has started");
        Runnable pqsareas_runnable = () -> {
            if(!pqsareas_execSer.isShutdown()){
                java.util.Date date1 = new java.util.Date();
                pqsareas.printStream.println(date1.toString() + " the process is running");
            } else {
                pqsareas_timeline.stop();
                pqsareas_btnStart.setDisable(false);
                pqsareas_btnStop.setDisable(true);
            }
        };
        pqsareas_timeline = createTimeline(5, pqsareas_runnable);
        pqsareas_timeline.play();

    }

    public void pqsareasStop(){
        java.util.Date date = new java.util.Date();
        pqsareas.printStream.println(date.toString() + " the process has been stopped externally");
        pqsareas_execSer.shutdownNow();
        pqsareas_btnStart.setDisable(false);
        pqsareas_btnStop.setDisable(true);
    }

    public void inputPQSareas1() {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            input1_path_pqsareas.setText(file.getName());
            pqsareas.setInput1Path(file.getAbsolutePath());
        }
    }

    public void inputPQSareas2(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            input2_path_pqsareas.setText(file.getName());
            pqsareas.setInput2Path(file.getAbsolutePath());
        }
    }

    public void pqsareasSetOutputDir(ActionEvent actionEvent) {
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            pqsareas_outputDir.setText(file.getName());
            pqsareas.setOutputPath(file.getAbsolutePath());
        }
    }

    public void pqsareasSetOutputName() {
        pqsareas.setOutputNamePQS(pqsareas_outputName.getText());
    }

    public void pqsareasSetAreaSize() {
        pqsareas.setAREA(Integer.parseInt(pqsareas_areaSize.getText()));
    }

    public void pqsareasInit(){
        psqareas_manual.setText(
            "PQSareas takes the genom and PQS positions and outputs two files:\n"+
            "a fasta file of PQS areas and a fasta file of PQS.\n\n"+
            "Program will run from several seconds to a few minutes depending on \n" +
            "a size and structure of the input file and given parameters.");
    }


    // clusters
    @FXML
    private Pane clusters_pnl;
    @FXML
    private Button clusters_btn;
    @FXML
    private Pane clusters_infoPnl;
    @FXML
    private Button clusters_info;
    @FXML
    private TextField clusters_inputPath;
    @FXML
    private TextField clusters_limit;
    @FXML
    private Label clusters_number;
    private ClustersCount clusters_count;
    @FXML
    private Text clusters_manual;
    @FXML
    public Button clusters_lastButtonClicked;
    @FXML
    public void clustersStart() throws IOException {
        clustersSetLimit();
        //clusters_count.loadCluster();
        Node[] nodes = new Node[Math.min(clusters_count.getLimit(), clusters_count.getLength())];
        /*
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Item.fxml"));
        Node node = (Node) loader.load();
        VisualizationController vc = loader.getController();
        vc.setInputPath(clustersCount.getInputPath());
         */
        pnItems.getChildren().clear();
        for (Integer i = 0; i < nodes.length; i++) {
            try {

                final int j = i;
                URL url = new File("src/main/resources/Item.fxml").toURI().toURL();
                FXMLLoader loader = new FXMLLoader(url);
                nodes[j] = loader.load();
                VisualizationController vc = loader.getController();
                vc.setInputPath(clusters_count.getInputPath());
                vc.setId(i);
                vc.setSize(clusters_count.getReferenceSeqs().get(i).size);
                vc.setReferenceSeq(clusters_count.getReferenceSeqs().get(i).reference_sequence);
                vc.setcontroller(this);

                nodes[i].setOnMouseEntered(event -> {
                    nodes[j].setStyle("-fx-background-color : #0A0E3F");
                });
                nodes[i].setOnMouseExited(event -> {
                    nodes[j].setStyle("-fx-background-color : #02030A");
                });
                //nodes[j].setId(i.toString());
                pnItems.getChildren().add(nodes[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void clustersSetInputPath() throws IOException {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            clusters_inputPath.setText(file.getName());
            clusters_count.setInputPath(file.getAbsolutePath());
            clusters_count.loadCluster();
            clusters_number.setText("(max " + clusters_count.getLength() + ")");
        }
    }

    private void clustersSetLimit() {
        if (clusters_limit.getText().isEmpty()) return;
        clusters_count.setLimit(Integer.parseInt(clusters_limit.getText()));
    }

    private void clustersInit(){
        clusters_manual.setText(
            "The program takes the list of clusters (output of CD-hit programs)\n" +
            "and outputs a table of visualizable clusters."
        );
    }

    // Blast Api
    @FXML
    private Button blastAPI_btn;
    @FXML
    private Pane blastAPI_pnl;
    @FXML
    private Button blastAPI_info;
    @FXML
    private Pane blastAPI_infoPnl;
    @FXML
    public TextField blastAPI_seq;
    @FXML
    private Label blastAPI_time;
    @FXML
    public ChoiceBox<String> blastAPI_database;
    @FXML
    private ChoiceBox<String> blastAPI_megablast;
    @FXML
    private Button blastAPI_link;
    @FXML
    private Text blastAPI_manual;
    private BlastApi blastAPI;

    public void blastAPISend() throws IOException {
        blastAPISetDatabase();
        blastAPISetMegablast();
        blastAPI.Send(blastAPI_seq.getText());
        blastAPI_time.setText("The results with id "+blastAPI.id+" will be available in estimated "+blastAPI.time+" seconds.");
        blastAPI_link.setText("Show results");
        blastAPI_link.setVisible(true);
    }

    public void blastAPIOpenInBrowser() {
        getHostServices().showDocument("https://blast.ncbi.nlm.nih.gov/Blast.cgi?CMD=Get&RID="+ blastAPI.id);
    }

    private void blastAPIInit(){
        blastAPI_database.getItems().setAll("nt", "refseq_rna", "pdbnt");
        blastAPI_megablast.getItems().setAll("on", "off");
        blastAPI_manual.setText("Programs sends the given seqnce to NCBI Blast program.");
    }

    private void blastAPISetDatabase(){
        blastAPI.setDatabase(blastAPI_database.getValue());
    }

    private void blastAPISetMegablast(){
        blastAPI.setMegablast(blastAPI_megablast.getValue());
    }

    // others
    private final FileChooser fileChooser = new FileChooser();
    private final DirectoryChooser directoryChooser = new DirectoryChooser();
    @FXML
    private Button btn_exit;

    private Timeline createTimeline(int timeFreq, Runnable r){
        Timeline tl =  new Timeline(new KeyFrame(Duration.seconds(timeFreq), event -> r.run()));
        tl.setCycleCount(Timeline.INDEFINITE);
        return tl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pqsfinder_btn.fire();
        cdhit = new CDhit(cdhit_area);
        cdhit2 = new CDhit2(cdhit2_area);
        pqsareas = new PQSareas(pqsareas_area);
        pqsfinder = new PQSfinder(pqsfinder_area);
        pqsfinderInit();
        pqsareasInit();
        cdhitInit();
        cdhit2Init();
        clustersInit();
        blastAPIInit();

        clusters_count = new ClustersCount();
        blastAPI = new BlastApi();

        btn_exit.setOnAction(actionEvent -> Platform.exit());
    }



    public void handleClicks(ActionEvent actionEvent) {
        if (actionEvent.getSource() == clusters_btn) {
            clusters_pnl.setStyle("-fx-background-color : "+background_color);
            clusters_pnl.toFront();
            return;
        }
        if (actionEvent.getSource() == clusters_info) {
            clusters_infoPnl.setStyle("-fx-background-color : "+background_color);
            clusters_infoPnl.toFront();
            return;
        }
        if (actionEvent.getSource() == pqsareas_btn) {
            pqsareas_pnl.setStyle("-fx-background-color : "+background_color);
            pqsareas_pnl.toFront();
            return;
        }
        if (actionEvent.getSource() == pqsareas_info) {
            pqsareas_infoPnl.setStyle("-fx-background-color : "+background_color);
            pqsareas_infoPnl.toFront();
            return;
        }
        if (actionEvent.getSource() == pqsfinder_btn) {
            pqsfinder_pnl.setStyle("-fx-background-color : "+background_color);
            pqsfinder_pnl.toFront();
            return;
        }
        if (actionEvent.getSource() == pqsfinder_info) {
            pqsfinder_infoPnl.setStyle("-fx-background-color : "+background_color);
            pqsfinder_infoPnl.toFront();
            return;
        }
        if(actionEvent.getSource()== cdhit_btn)
        {
            cdhit_pnl.setStyle("-fx-background-color : "+background_color);
            cdhit_pnl.toFront();
            return;
        }
        if(actionEvent.getSource()== cdhit_info)
        {
            cdhit_infoPnl.setStyle("-fx-background-color : "+background_color);
            cdhit_infoPnl.toFront();
            return;
        }
        if(actionEvent.getSource()== cdhit2_btn)
        {
            cdhit2_pnl.setStyle("-fx-background-color : "+background_color);
            cdhit2_pnl.toFront();
            return;
        }
        if(actionEvent.getSource()== cdhit2_info)
        {
            cdhit2_infoPnl.setStyle("-fx-background-color : "+background_color);
            cdhit2_infoPnl.toFront();
            return;
        }
        if(actionEvent.getSource()== blastAPI_btn)
        {
            blastAPI_pnl.setStyle("-fx-background-color : "+background_color);
            blastAPI_pnl.toFront();
            return;
        }
        if(actionEvent.getSource()== blastAPI_info)
        {
            blastAPI_infoPnl.setStyle("-fx-background-color : "+background_color);
            blastAPI_infoPnl.toFront();
            return;
        }
    }

    @Override
    public void start(Stage stage) throws Exception {

    }
}
