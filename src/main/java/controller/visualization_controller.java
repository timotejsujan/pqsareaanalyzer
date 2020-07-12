package controller;

import javafx.scene.control.*;
import model.cluster;
import model.cluster_visualization;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    private String reference_seq;
    private int area_size;
    public base_controller contr;
    @FXML
    private Label cluster_id;
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
        file_chooser.setInitialFileName("cluster_"+id+"_"+contr.clusters_logo_contr.short_input_path()+".png");
        File file = file_chooser.showSaveDialog(Main.primary_stage);
        if (file != null) {
            cv = new cluster_visualization(new cluster(id, new File(inputPath)));
            ImageIO.write(cv.get_buffered_image(), "png", file);
        }
    }

    public void copy_to_blast(ActionEvent event) {
        contr.blastapi_contr.sequence.setText(reference_seq);
        contr.blast_local_contr.sequence.setText(reference_seq);
        if (contr.clusters_logo_contr.last_click_btn != null) {
            contr.clusters_logo_contr.last_click_btn.setText("Copy to Blast");
        }
        Button b = (Button) event.getSource();
        b.setText("Copied");
        contr.clusters_logo_contr.last_click_btn = b;
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
        reference_seq = s;
    }

    public void show_sequence() {
        ButtonType ok_button = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().getButtonTypes().add(ok_button);
        dialog.setTitle("Reference sequence");
        dialog.setHeaderText(null);
        dialog.setContentText(get_ref_string());
        dialog.initOwner(Main.primary_stage);
        dialog.show();
    }

    private String get_ref_string(){
        String content = "left area\n";
        content += reference_seq.substring(0, area_size);
        content += "\npqs\n";
        content += reference_seq.substring(area_size, reference_seq.length() - area_size);
        content += "\nright area\n";
        content += reference_seq.substring(reference_seq.length() - area_size);
        return content;
    }

    public void set_area_size(int area_size) {
        this.area_size = area_size;
    }
}
