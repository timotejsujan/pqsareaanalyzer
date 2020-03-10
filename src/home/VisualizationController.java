package home;

import internal.Cluster;
import internal.ClusterVisualization;
import internal.ClustersCount;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;

import javax.swing.*;
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
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void clusterVisualize(ActionEvent actionEvent) throws IOException {
        final Node source = (Node) actionEvent.getSource();
        //String id = source.getId();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    ClusterVisualization cv = new ClusterVisualization(new Cluster(id, new File(inputPath)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public void setId(int id) {
        this.id = id;
    }
}
