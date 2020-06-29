package home;

import external.cluster;
import external.cluster_visualization;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Timotej Sujan
 */
public class VisualizationController implements Initializable {
    private String inputPath;
    private Integer id;
    private cluster_visualization cv;
    private String referenceSeq;
    private Controller controller;
    @FXML
    private Label cluster_id;
    @FXML
    private Label cluster_refSeq;
    @FXML
    private Label cluster_size;
    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
    public void clusterVisualize(ActionEvent actionEvent) throws IOException {
        //final Node source = (Node) actionEvent.getSource();
        //String id = source.getId();
        cv = new cluster_visualization(new cluster(id, new File(inputPath)));
        cv.showSequence();
    }

    public void clusterExport(ActionEvent actionEvent) throws IOException {
        cv = new cluster_visualization(new cluster(id, new File(inputPath)));
        //cv.showSequence(false);
        cv.export();
    }

    public void copyToBlast(ActionEvent event){
        controller.blastAPI_seq.setText(referenceSeq);
        Button lastButtonClicked = controller.clusters_lastButtonClicked;
        if (lastButtonClicked != null){
            lastButtonClicked.setText("Copy to Blast");
        }
        Button b = (Button) event.getSource();
        b.setText("Copied");
        controller.clusters_lastButtonClicked = b;
    }

    void setInputPath(String s) {
        inputPath = s;
    }

    public void setId(int n) {
        id = n;
        cluster_id.setText(id.toString());
    }

    public void setSize(int size) {
        cluster_size.setText(Integer.toString(size));
    }

    public void setReferenceSeq(String s) {
        referenceSeq = s;
        cluster_refSeq.setText(s);
    }

    public void setcontroller(Controller c) {
        controller = c;
    }
}
