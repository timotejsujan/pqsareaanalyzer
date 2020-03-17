package home;

import external.CDhit;
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
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
    @FXML
    private Button btnPQSfinder;

    @FXML
    private Button btnCDhit;

    @FXML
    private Button btnClusters;

    @FXML
    private Button btnPQSareas;

    @FXML
    private Button btnBlastApi;

    @FXML
    private Button btnPackages;

    @FXML
    private Button btnSettings;

    @FXML
    private Button btnSignout;

    @FXML
    private Pane pnlClusters;

    @FXML
    private Pane pnlOrders;

    @FXML
    private Pane pnlPQSfinder;

    @FXML
    private Pane pnlPQSareas;

    @FXML
    private Pane pnlBlastApi;

    @FXML
    private TextField input_path_pqsfinder;

    @FXML
    private TextField input_path_clusters;

    @FXML
    private TextField input_path_pqs;

    @FXML
    private TextField input1_path_pqsareas;

    @FXML
    private TextField input2_path_pqsareas;

    @FXML
    private TextField output_name_pqsareas;

    @FXML
    private TextField input_path_cdhit;

    @FXML
    private TextField output_name_cdhit;

    @FXML
    private Button btnPQSfinder_input1;

    @FXML
    private Button btnPQSfinder_input2;

    @FXML
    private Button btnPQSareas_input;

    @FXML
    private Button btnCDhit_input;

    @FXML
    private Button btnPQSfinder_output;

    @FXML
    private Button btnPQSareas_output;

    @FXML
    private Button btnCDhit_output;

    @FXML
    private TextField output_path_pqsfinder;

    @FXML
    private TextField output_name_pqsfinder;

    @FXML
    private TextField output_path_pqsareas;

    @FXML
    private TextField fieldPQSareas_area;

    @FXML
    private TextField fieldClusters_limit;

    @FXML
    private TextField output_path_cdhit;

    @FXML
    private TextArea CDhit_area;

    @FXML
    private TextArea PQSareas_area;

    @FXML
    private TextArea PQSfinder_area;

    @FXML
    private TextField seq_blast;

    @FXML
    private Label blast_info;

    @FXML
    private Hyperlink blast_link;

    private CDhit cdhit;

    private PQSfinder pqsfinder;

    private PQSareas pqsareas;

    private BlastApi blastapi;

    private ClustersCount clustersCount;

    private Thread CDhit_thread;

    private Thread PQSareas_thread;

    private Thread PQSfinder_thread;

    private final FileChooser fileChooser = new FileChooser();

    private final DirectoryChooser directoryChooser = new DirectoryChooser();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnPQSfinder.fire();
        cdhit = new CDhit(CDhit_area);
        pqsareas = new PQSareas(PQSareas_area);
        pqsfinder = new PQSfinder(PQSfinder_area);
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
        if (actionEvent.getSource() == btnPQSfinder) {
            pnlPQSfinder.setStyle("-fx-background-color : #53639F");
            pnlPQSfinder.toFront();
            return;
        }
        if(actionEvent.getSource()== btnCDhit)
        {
            pnlOrders.setStyle("-fx-background-color : #53639F");
            pnlOrders.toFront();
            return;
        }
        if(actionEvent.getSource()== btnBlastApi)
        {
            pnlBlastApi.setStyle("-fx-background-color : #53639F");
            pnlBlastApi.toFront();
            return;
        }
    }

    public void startPQSfinder() {
        setOutputNamePQSfinder();
        PQSfinder_thread = new Thread(() -> {
            try {
                pqsfinder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        PQSfinder_thread.start();
    }

    public void stopPQSfinder(ActionEvent actionEvent){
        PQSfinder_thread.interrupt();
    }

    public void inputPQSfinder(ActionEvent actionEvent) {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            input_path_pqsfinder.setText(file.getName());
            pqsfinder.setInputPath(file.getAbsolutePath());
        }
    }

    public void outputPQSfinder(ActionEvent actionEvent) {
        File file = directoryChooser.showDialog(null);
        if (file != null) {
            output_path_pqsfinder.setText(file.getName());
            pqsfinder.setOutputPath(file.getAbsolutePath());
        }
    }

    public void setOutputNamePQSfinder() {
        pqsfinder.setOutputName(output_name_pqsfinder.getText());
    }

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

    public void sendBlast(ActionEvent actionEvent) throws IOException {
        blastapi.Send(seq_blast.getText());
        blast_info.setText(blastapi.time);
        blast_link.setText(blastapi.id);
    }

    public void openInBrowser() {
        getHostServices().showDocument("https://blast.ncbi.nlm.nih.gov/Blast.cgi?CMD=Get&RID="+blastapi.id);
    }

    @Override
    public void start(Stage stage) throws Exception {

    }
}
