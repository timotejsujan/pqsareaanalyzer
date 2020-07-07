package home;

import external.cluster;
import external.cluster_visualization;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Timotej Sujan
 */
public class visualization_controller implements Initializable {
    private String inputPath;
    private Integer id;
    private cluster_visualization cv;
    private String referenceSeq;
    private clusters_controller cluster_contr;
    public blastapi_controller blastapi_contr;
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

    public void show() throws IOException {
        cv = new cluster_visualization(new cluster(id, new File(inputPath)));
        cv.show_sequence();
    }

    public void export() throws IOException {
        FileChooser file_chooser = new FileChooser();
        file_chooser.setInitialFileName("cluster_"+id+"_"+cluster_contr.input_path.getText());
        File file = file_chooser.showSaveDialog(null);
        if (file != null) {
            cv = new cluster_visualization(new cluster(id, new File(inputPath)));
            ImageIO.write(cv.get_buffered_image(), "png", file);
        }
    }

    public void copy_to_blast(ActionEvent event) {
        blastapi_contr.sequence.setText(referenceSeq);
        if (cluster_contr.last_click_btn != null) {
            cluster_contr.last_click_btn.setText("Copy to Blast");
        }
        Button b = (Button) event.getSource();
        b.setText("Copied");
        cluster_contr.last_click_btn = b;
    }

    void set_input_path(String s) {
        inputPath = s;
    }

    public void set_id(int n) {
        id = n;
        cluster_id.setText(id.toString());
    }

    public void set_size(int size) {
        cluster_size.setText(Integer.toString(size));
    }

    public void set_reference_sequence(String s) {
        referenceSeq = s;
        cluster_refSeq.setText(s);
    }

    public void set_controllers(clusters_controller c, blastapi_controller b) {
        cluster_contr = c;
        blastapi_contr = b;
    }
}
