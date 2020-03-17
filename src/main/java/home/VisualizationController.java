package home;

import internal.Cluster;
import internal.ClusterVisualization;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
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

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public void setId(int id) {
        this.id = id;
    }
}
