package home;

import external.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller extends Application implements Initializable {

    @FXML
    private VBox pnItems;

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
            showAlert(Alert.AlertType.ERROR, cdhit_pnl.getScene().getWindow(), "Not valid!",
                    "Values are NOT valid, something is missing!");
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
                pqsfinder.print_stream.println(e.toString());
                pqsfinder_execSer.shutdownNow();
                e.printStackTrace();
            }
        });
        //pqsfinder_thread.start();
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


    // CD hit
    @FXML
    private Pane cdhit_pnl, cdhit_infoPnl;
    @FXML
    private Button cdhit_btn, cdhit_btnStart, cdhit_btnStop, cdhit_info;
    @FXML
    private TextField cdhit_inputPath, cdhit_outputDir, cdhit_outputName, cdhit_params, input_path_pqs;
    @FXML
    private TextArea cdhit_area;
    private cd_hit_est cdhit;
    private ExecutorService cdhit_execSer = Executors.newSingleThreadExecutor();
    private Timeline cdhit_timeline;
    @FXML
    private Text cdhit_manual;

    public void cdhitStart() {
        cdhitSetOutputDir();
        if (!cdhit.is_base_valid() || !cdhit.is_valid()) {
            cdhit.print_stream.println("values are NOT valid, something is missing!");
            return;
        }
        cdhitSetParams();
        if (cdhit_execSer.isShutdown()) {
            cdhit_execSer = Executors.newSingleThreadExecutor();
        }
        cdhit_execSer.execute(() -> {
            try {
                cdhit.start();
                cdhit_execSer.shutdownNow();
            } catch (IOException e) {
                cdhit.print_stream.println(e.toString());
                cdhit_execSer.shutdownNow();
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
        cdhit.print_stream.println(date.toString() + " the process has been stopped externally");
    }

    public void cdhitSetInputPath() {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            cdhit_inputPath.setText(file.getName());
            cdhit.set_input_path(file.getAbsolutePath());
            //parameters_cdhit.appendText(" -i "+file.getPath()+" ");
        }
    }

    private void cdhitSetOutputDir() {
        cdhit.set_output_name(cdhit_outputDir.getText());
    }

    private void cdhitSetParams() {
        cdhit.setParameters(cdhit_params.getText());
    }

    public void cdhitSetInputPQS() {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            input_path_pqs.setText(file.getName());
            cdhit.setInputPathPqs(file.getPath());
        }
    }

    public void cdhitSetOutputName() {
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            cdhit_outputName.setText(file.getName());
            cdhit.set_output_path(file.getAbsolutePath());
        }
    }

    public void cdhitOpenDoc() throws IOException {
        Desktop desktop = Desktop.getDesktop();
        File f = new File(Main.jar_folder_path+"/documentation/cdhit.pdf");
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
    private Pane cdhit2_pnl, cdhit2_infoPnl;
    @FXML
    private Button cdhit2_btn, cdhit2_info;
    @FXML
    private TextField cdhit2_inputPath, cdhit2_inputPath2, cdhit2_outputName, cdhit2_outputDir, cdhit2_params;
    @FXML
    private TextField cdhit2_InputPathPQS;
    @FXML
    private TextArea cdhit2_area;
    private cd_hit_est_2D cdhit2;
    private Thread cdhit2_thread;
    @FXML
    private Text cdhit2_manual;
    public void cdhit2Start() {
        cdhit2SetOutputName();
        if (!cdhit2.is_base_valid() || cdhit2.is_valid()) {
            cdhit2.print_stream.println("values are NOT valid, something is missing!");
            return;
        }
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

    public void cdhit2Stop(){
        cdhit2_thread.interrupt();
    }

    public void cdhit2SetInputPath() {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            cdhit2_inputPath.setText(file.getName());
            cdhit2.set_input_path(file.getAbsolutePath());
        }
    }

    public void cdhit2SetInputPath2() {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            cdhit2_inputPath2.setText(file.getName());
            cdhit2.setInputPath2(file.getAbsolutePath());
        }
    }

    public void cdhit2SetOutputName() {
        cdhit2.set_output_name(cdhit2_outputName.getText());
    }

    public void cdhit2SetParams() {
        cdhit2.setParams(cdhit2_params.getText());
    }

    public void cdhit2SetInputPQS() {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            cdhit2_InputPathPQS.setText(file.getName());
            cdhit2.setInputPathPqs(file.getAbsolutePath());
        }
    }

    public void cdhit2SetOutputDir() {
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            cdhit2_outputDir.setText(file.getName());
            cdhit2.set_output_path(file.getAbsolutePath());
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
    private Pane pqsareas_pnl, pqsareas_infoPnl;
    @FXML
    private TextField input1_path_pqsareas, input2_path_pqsareas, pqsareas_outputName, pqsareas_outputDir;
    @FXML
    private TextField pqsareas_areaSize;
    @FXML
    private Button pqsareas_btn, pqsareas_btnStart, pqsareas_btnStop, pqsareas_info;
    @FXML
    private Text psqareas_manual;
    @FXML
    private TextArea pqsareas_area;
    private pqs_areas pqsareas;
    private ExecutorService pqsareas_execSer = Executors.newSingleThreadExecutor();
    private Timeline pqsareas_timeline = null;

    public void pqsareasStart() {
        pqsareasSetOutputName();
        pqsareas.set_program_path("dummy");
        if (!pqsareas.is_base_valid() || !pqsareas.is_valid()) {
            pqsareas.print_stream.println("values are NOT valid, something is missing!");
            return;
        }
        pqsareasSetAreaSize();
        if (pqsareas_execSer.isShutdown()) {
            pqsareas_execSer = Executors.newSingleThreadExecutor();
        }
        pqsareas_execSer.execute(() -> {
            try {
                pqsareas.start();
                pqsareas_execSer.shutdownNow();
            } catch (IOException e) {
                pqsareas.print_stream.println(e.toString());
                pqsareas_execSer.shutdownNow();
                e.printStackTrace();
            }
        });
        pqsareas_btnStart.setDisable(true);
        pqsareas_btnStop.setDisable(false);
        java.util.Date date = new java.util.Date();
        pqsareas.print_stream.println(date.toString() + " the process has started");
        Runnable pqsareas_runnable = () -> {
            if(!pqsareas_execSer.isShutdown()){
                java.util.Date date1 = new java.util.Date();
                pqsareas.print_stream.println(date1.toString() + " the process is running");
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
        pqsareas.print_stream.println(date.toString() + " the process has been stopped externally");
        pqsareas_execSer.shutdownNow();
        pqsareas_btnStart.setDisable(false);
        pqsareas_btnStop.setDisable(true);
    }

    public void inputPQSareas1() {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            input1_path_pqsareas.setText(file.getName());
            pqsareas.set_input_path(file.getAbsolutePath());
        }
    }

    public void inputPQSareas2() {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            input2_path_pqsareas.setText(file.getName());
            pqsareas.setInput2Path(file.getAbsolutePath());
        }
    }

    public void pqsareasSetOutputDir() {
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            pqsareas_outputDir.setText(file.getName());
            pqsareas.set_output_path(file.getAbsolutePath());
        }
    }

    public void pqsareasSetOutputName() {
        pqsareas.setOutputNamePQS(pqsareas_outputName.getText());
    }

    public void pqsareasSetAreaSize() {
        pqsareas.set_area(Integer.parseInt(pqsareas_areaSize.getText()));
    }

    private void pqsareasInit(){
        psqareas_manual.setText(
            "PQSareas takes the genom and PQS positions and outputs two files:\n"+
            "a fasta file of PQS areas and a fasta file of PQS.\n\n"+
            "Program will run from several seconds to a few minutes depending on \n" +
            "a size and structure of the input file and given parameters.");
    }


    // clusters
    @FXML
    private Pane clusters_pnl, clusters_infoPnl;
    @FXML
    private Button clusters_btn, clusters_info;
    @FXML
    public Button clusters_lastButtonClicked;
    @FXML
    private TextField clusters_inputPath, clusters_limit;
    @FXML
    private Label clusters_number;
    private external.clusters_count clusters_count;
    @FXML
    private Text clusters_manual;
    @FXML
    public void clustersStart() {
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
        for (int i = 0; i < nodes.length; i++) {
            try {

                final int j = i;
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Item.fxml"));
                //URL url = new File("src/main/resources/Item.fxml").toURI().toURL();
                //FXMLLoader loader = new FXMLLoader(url);
                nodes[j] = loader.load();
                VisualizationController vc = loader.getController();
                vc.setInputPath(clusters_count.getInputPath());
                vc.setId(i);
                vc.setSize(clusters_count.getReferenceSeqs().get(i).size);
                vc.setReferenceSeq(clusters_count.getReferenceSeqs().get(i).reference_sequence);
                vc.setcontroller(this);

                nodes[i].setOnMouseEntered(event -> nodes[j].setStyle("-fx-background-color : #FFFFFF"));
                nodes[i].setOnMouseExited(event -> nodes[j].setStyle("-fx-background-color : #FFFFFF"));
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
            clusters_count.load_cluster();
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
    private Button blastAPI_btn, blastAPI_info, blastAPI_link;
    @FXML
    private Pane blastAPI_pnl, blastAPI_infoPnl;
    @FXML
    public TextField blastAPI_seq;
    @FXML
    private Label blastAPI_time;
    @FXML
    public ChoiceBox<String> blastAPI_database, blastAPI_megablast;
    @FXML
    private Text blastAPI_manual;
    private blast_api blastAPI;

    public void blastAPISend() throws Exception {
        blastAPISetDatabase();
        blastAPISetMegablast();
        blastAPI.send(blastAPI_seq.getText());
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
        blastAPI.set_database(blastAPI_database.getValue());
    }

    private void blastAPISetMegablast(){
        blastAPI.set_megablast(blastAPI_megablast.getValue());
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
        String cdhit_path = "";
        if (Main.isWindows()) cdhit_path = "cdhit-windows";
        if (Main.isUnix()) cdhit_path = "cdhit-linux";
        if (Main.isMac()) cdhit_path = "cdhit-osx";

        pqsfinder_btn.fire();
        cdhit = new cd_hit_est(cdhit_area);
        cdhit.set_program_path(Main.jar_folder_path + "/"+cdhit_path+"/cd-hit-est");
        cdhit2 = new cd_hit_est_2D(cdhit2_area);
        cdhit2.set_program_path(Main.jar_folder_path + "/"+cdhit_path+"/cd-hit-est-2d");
        pqsareas = new pqs_areas(pqsareas_area);
        pqsfinder = new pqsfinder(pqsfinder_area);
        pqsfinder.set_program_path(Main.jar_folder_path + "/g4.R");

        pqsfinderInit();
        pqsareasInit();
        cdhitInit();
        cdhit2Init();
        clustersInit();
        blastAPIInit();

        clusters_count = new clusters_count();
        blastAPI = new blast_api();

        btn_exit.setOnAction(actionEvent -> Platform.exit());
    }



    public void handleClicks(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        Pane current_pnl = new Pane();
        if (source == clusters_btn) current_pnl = clusters_pnl;
        else if (source == clusters_info) current_pnl = clusters_infoPnl;
        else if (source == pqsareas_btn) current_pnl = pqsareas_pnl;
        else if (source == pqsareas_info) current_pnl = pqsareas_infoPnl;
        else if (source == pqsfinder_btn) current_pnl = pqsfinder_pnl;
        else if (source == pqsfinder_info) current_pnl = pqsfinder_infoPnl;
        else if(source== cdhit_btn) current_pnl = cdhit_pnl;
        else if(source== cdhit_info) current_pnl = cdhit_infoPnl;
        else if(source== cdhit2_btn) current_pnl = cdhit2_pnl;
        else if(source== cdhit2_info) current_pnl = cdhit2_infoPnl;
        else if(source== blastAPI_btn) current_pnl = blastAPI_pnl;
        else if(source== blastAPI_info) current_pnl = blastAPI_infoPnl;

        current_pnl.toFront();
    }

    @Override
    public void start(Stage stage) {

    }

    public void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }
}
