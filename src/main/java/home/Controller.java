package home;

import external.CDhit;
import external.CDhit2;
import external.PQSfinder;

import internal.BlastApi;
import internal.ClustersCount;
import internal.PQSareas;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller extends Application implements Initializable {

    @FXML
    private VBox pnItems = null;

    // pqs finder
    @FXML
    private Pane pqsfinder_pnl;
    @FXML
    private TextField pqsfinder_inputPath;
    @FXML
    private Button pqsfinder_btn;
    @FXML
    private TextField pqsfinder_outputDir;
    @FXML
    private TextField pqsfinder_outputName;
    @FXML
    private ComboBox<String> pqsfinder_strand;
    @FXML
    private TextField pqsfinder_overlapping;
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
    private PQSfinder pqsfinder;
    private Thread pqsfinder_thread;
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

    public void pqsfinderInit(){
        pqsfinder_strand.getItems().setAll("", "*", "+", "-");
        tt_strand.setText("Strand specification (+, - or *).");
        pqsfinder_strand.setTooltip(tt_strand);

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
    }

    public void pqsfinderStart() {
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
        pqsfinder_thread = new Thread(() -> {
            try {
                pqsfinder.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        pqsfinder_thread.start();
    }

    public void pqsfinderStop(){
        pqsfinder_thread.interrupt();
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

    public void pqsfinderSetOutputName() {
        pqsfinder.setOutputName(pqsfinder_outputName.getText());
    }

    public void pqsfinderSetStrand() {
        pqsfinder.setStrand(pqsfinder_strand.getValue());
    }

    public void pqsfinderSetOverlapping() {
        pqsfinder.setOverlapping(pqsfinder_overlapping.getText());
    }

    public void pqsfinderSetMax_len() {
        pqsfinder.setMax_len(pqsfinder_maxLen.getText());
    }

    public void pqsfinderSetMin_score() {
        pqsfinder.setMin_score(pqsfinder_minScore.getText());
    }

    public void pqsfinderSetRun_min_len() {
        pqsfinder.setRun_min_len(pqsfinder_runMinLen.getText());
    }

    public void pqsfinderSetRun_max_len() {
        pqsfinder.setRun_max_len(pqsfinder_runMaxLen.getText());
    }

    public void pqsfinderSetLoop_min_len() {
        pqsfinder.setLoop_min_len(pqsfinder_loopMinLen.getText());
    }

    public void pqsfinderSetLoop_max_len() {
        pqsfinder.setLoop_max_len(pqsfinder_loopMaxLen.getText());
    }

    public void pqsfinderSetMax_bulges() {
        pqsfinder.setMax_bulges(pqsfinder_maxBulges.getText());
    }

    public void pqsfinderSetMax_mismatches() {
        pqsfinder.setMax_mismatches(pqsfinder_maxMismatches.getText());
    }

    public void pqsfinderSetMax_defects() {
        pqsfinder.setMax_defects(pqsfinder_maxDefects.getText());
    }


    // CD hit
    @FXML
    private Pane pnlCDhit;
    @FXML
    private Button btnCDhit;
    @FXML
    private TextField input_path_cdhit;
    @FXML
    private TextField output_name_cdhit;
    @FXML
    private TextField output_path_cdhit;
    @FXML
    private TextArea CDhit_area;
    private CDhit cdhit;
    private Thread CDhit_thread;
    @FXML
    private TextField input_path_pqs;
    public void startCDhit(ActionEvent actionEvent) {
        setOutputNameCDhit();
        CDhit_thread = new Thread(() -> {
            try {
                cdhit.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        CDhit_thread.start();

    }

    public void stopCDhit(ActionEvent actionEvent){
        CDhit_thread.interrupt();
    }

    public void inputCDhit(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            input_path_cdhit.setText(file.getName());
            cdhit.setInputPath(file.getAbsolutePath());
        }
    }

    public void setOutputNameCDhit() {
        cdhit.setOutputName(output_name_cdhit.getText());
    }

    public void inputPQS(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            input_path_pqs.setText(file.getName());
            cdhit.setInputPathPqs(file.getAbsolutePath());
        }
    }

    public void outputCDhit(ActionEvent actionEvent) {
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            output_path_cdhit.setText(file.getName());
            cdhit.setOutputPath(file.getAbsolutePath());
        }
    }

    // CD hit 2
    @FXML
    private Pane pnlCDhit2;
    @FXML
    private Button btnCDhit2;
    @FXML
    private TextField input_path_cdhit2;
    @FXML
    private TextField input2_path_cdhit2;
    @FXML
    private TextField output_name_cdhit2;
    @FXML
    private TextField output_path_cdhit2;
    @FXML
    private TextArea CDhit2_area;
    private CDhit2 cdhit2;
    private Thread CDhit2_thread;
    @FXML
    private TextField input_path_pqs2;
    public void startCDhit2(ActionEvent actionEvent) {
        setOutputNameCDhit2();
        CDhit2_thread = new Thread(() -> {
            try {
                cdhit2.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        CDhit2_thread.start();

    }

    public void stopCDhit2(ActionEvent actionEvent){
        CDhit2_thread.interrupt();
    }

    public void inputCDhit2(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            input_path_cdhit2.setText(file.getName());
            cdhit2.setInputPath(file.getAbsolutePath());
        }
    }

    public void input2CDhit2(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            input2_path_cdhit2.setText(file.getName());
            cdhit2.setInputPath2(file.getAbsolutePath());
        }
    }

    public void setOutputNameCDhit2() {
        cdhit2.setOutputName(output_name_cdhit2.getText());
    }

    public void inputPQS2(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            input_path_pqs2.setText(file.getName());
            cdhit2.setInputPathPqs(file.getAbsolutePath());
        }
    }

    public void outputCDhit2(ActionEvent actionEvent) {
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            output_path_cdhit2.setText(file.getName());
            cdhit2.setOutputPath(file.getAbsolutePath());
        }
    }


    // pqs areas
    @FXML
    private Pane pnlPQSareas;
    @FXML
    private TextField input1_path_pqsareas;
    @FXML
    private TextField input2_path_pqsareas;
    @FXML
    private TextField output_name_pqsareas;
    @FXML
    private TextField output_path_pqsareas;
    @FXML
    private Button btnPQSareas;
    @FXML
    private TextField fieldPQSareas_area;
    @FXML
    private TextArea PQSareas_area;
    private PQSareas pqsareas;
    private Thread PQSareas_thread;

    public void startPQSareas(ActionEvent actionEvent) {
        areaPQSareas();
        setOutputNamePQSareas();
        PQSareas_thread = new Thread(() -> {
            try {
                pqsareas.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        PQSareas_thread.start();
    }

    public void stopPQSareas(ActionEvent actionEvent){
        PQSareas_thread.interrupt();
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

    public void outputPQSareas(ActionEvent actionEvent) {
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            output_path_pqsareas.setText(file.getName());
            pqsareas.setOutputPath(file.getAbsolutePath());
        }
    }

    public void setOutputNamePQSareas() {
        pqsareas.setOutputNamePQS(output_name_pqsareas.getText());
    }

    public void areaPQSareas() {
        pqsareas.setAREA(Integer.parseInt(fieldPQSareas_area.getText()));
    }


    // clusters
    @FXML
    private Pane pnlClusters;
    @FXML
    private Button btnClusters;
    @FXML
    private TextField input_path_clusters;
    @FXML
    private TextField fieldClusters_limit;
    private ClustersCount clustersCount;
    @FXML
    public void startClusters(ActionEvent actionEvent) throws IOException {
        Node[] nodes = new Node[Math.min(clustersCount.getLimit(), clustersCount.getLength())];
        /*
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Item.fxml"));
        Node node = (Node) loader.load();
        VisualizationController vc = loader.getController();
        vc.setInputPath(clustersCount.getInputPath());
         */
        for (Integer i = 0; i < nodes.length; i++) {
            try {

                final int j = i;
                URL url = new File("src/main/resources/Item.fxml").toURI().toURL();
                FXMLLoader loader = new FXMLLoader(url);
                nodes[j] = (Node) loader.load();
                VisualizationController vc = loader.getController();
                vc.setInputPath(clustersCount.getInputPath());
                vc.setId(i);

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

    public void stopClusters(ActionEvent actionEvent){
        //PQSfinder_thread.interrupt();
    }

    public void inputClusters(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            input_path_clusters.setText(file.getName());
            clustersCount.setInputPath(file.getAbsolutePath());
        }
    }

    public void limitClusters(ActionEvent actionEvent) {
        clustersCount.setLimit(Integer.parseInt(fieldClusters_limit.getText()));
    }

    // Blast Api
    @FXML
    private Button btnBlastApi;
    @FXML
    private Pane pnlBlastApi;
    @FXML
    private TextField seq_blast;
    @FXML
    private Label blast_info;
    @FXML
    private Hyperlink blast_link;
    private BlastApi blastapi;

    public void sendBlast(ActionEvent actionEvent) throws IOException {
        blastapi.Send(seq_blast.getText());
        blast_info.setText(blastapi.time);
        blast_link.setText(blastapi.id);
    }

    public void openInBrowser() {
        getHostServices().showDocument("https://blast.ncbi.nlm.nih.gov/Blast.cgi?CMD=Get&RID="+blastapi.id);
    }

    // others
    private final FileChooser fileChooser = new FileChooser();
    private final DirectoryChooser directoryChooser = new DirectoryChooser();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pqsfinder_btn.fire();
        cdhit = new CDhit(CDhit_area);
        cdhit2 = new CDhit2(CDhit2_area);
        pqsareas = new PQSareas(PQSareas_area);
        pqsfinder = new PQSfinder(pqsfinder_area);
        pqsfinderInit();

        clustersCount = new ClustersCount();
        blastapi = new BlastApi();

    }



    public void handleClicks(ActionEvent actionEvent) {
        if (actionEvent.getSource() == btnClusters) {
            pnlClusters.setStyle("-fx-background-color : #53639F");
            pnlClusters.toFront();
            return;
        }
        if (actionEvent.getSource() == btnPQSareas) {
            pnlPQSareas.setStyle("-fx-background-color : #53639F");
            pnlPQSareas.toFront();
            return;
        }
        if (actionEvent.getSource() == pqsfinder_btn) {
            pqsfinder_pnl.setStyle("-fx-background-color : #53639F");
            pqsfinder_pnl.toFront();
            return;
        }
        if(actionEvent.getSource()== btnCDhit)
        {
            pnlCDhit.setStyle("-fx-background-color : #53639F");
            pnlCDhit.toFront();
            return;
        }
        if(actionEvent.getSource()== btnCDhit2)
        {
            pnlCDhit2.setStyle("-fx-background-color : #53639F");
            pnlCDhit2.toFront();
            return;
        }
        if(actionEvent.getSource()== btnBlastApi)
        {
            pnlBlastApi.setStyle("-fx-background-color : #53639F");
            pnlBlastApi.toFront();
            return;
        }
    }

    @Override
    public void start(Stage stage) throws Exception {

    }
}
