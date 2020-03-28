package home;

import internal.Cluster;
import internal.ClusterVisualization;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    private ClusterVisualization cv;
    private Integer size;
    private String referenceSeq;
    private Controller controller;
    @FXML
    private Label cluster_id;
    @FXML
    private Label cluster_refSeq;
    @FXML
    private Label cluster_size;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
    public void clusterVisualize(ActionEvent actionEvent) throws IOException {
        //final Node source = (Node) actionEvent.getSource();
        //String id = source.getId();
        cv = new ClusterVisualization(new Cluster(id, new File(inputPath)));
        cv.showSequence();
    }

    public void clusterExport(ActionEvent actionEvent) throws IOException {
        cv = new ClusterVisualization(new Cluster(id, new File(inputPath)));
        //cv.showSequence(false);
        cv.export();
    }

    public void copyToBlast(){
        this.controller.seq_blast.setText(referenceSeq);
    }

    void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public void setId(int id) {
        this.id = id;
        cluster_id.setText(this.id.toString());
    }

    public void setSize(int size) {
        this.size = size;
        cluster_size.setText(this.size.toString());
    }

    public void setReferenceSeq(String referenceSeq) {
        this.referenceSeq = referenceSeq;
        cluster_refSeq.setText(referenceSeq);
    }

    public void setcontroller(Controller controller) {
        this.controller = controller;
    }
}
