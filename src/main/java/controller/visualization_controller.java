package controller;

import javafx.scene.control.*;
import javafx.stage.Window;
import model.cluster;
import model.cluster_logo;
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
    private cluster_logo cv;
    private String inputPath, reference_seq;
    private int area_size, id;
    public base_controller contr;
    @FXML private Label cluster_id, cluster_size;

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void show() throws IOException {
        cluster cluster = new cluster(id, new File(inputPath));
        if (!cluster.possible){
            showAlert(Alert.AlertType.ERROR, Main.primary_stage, "Not possible",
                    "Sequences in this cluster don't have the same area size. " +
                            "You probably compared two cluster files with different " +
                            "area sizes in program CD-HIT-EST-2D.");
            return;
        }
        cv = new cluster_logo(cluster);
        cv.show_sequence();
    }

    public void export() throws IOException {
        FileChooser file_chooser = new FileChooser();
        file_chooser.setInitialFileName("cluster_"+id+"_"+contr.cluster_logo_contr.short_input_path()+".png");
        File file = file_chooser.showSaveDialog(Main.primary_stage);
        if (file != null) {
            cluster cluster = new cluster(id, new File(inputPath));
            if (!cluster.possible){
                showAlert(Alert.AlertType.ERROR, Main.primary_stage, "Not possible",
                        "Sequences in this cluster don't have the same area size. " +
                                "You probably compared two cluster files with different " +
                                "area sizes in program CD-HIT-EST-2D.");
                return;
            }
            cv = new cluster_logo(cluster);
            ImageIO.write(cv.get_buffered_image(), "png", file);
        }
    }

    public void copy_to_blast(ActionEvent event) {
        contr.blast_web_contr.sequence.setText(reference_seq);
        contr.blast_local_contr.sequence.setText(reference_seq);
        if (contr.cluster_logo_contr.last_click_btn != null) {
            contr.cluster_logo_contr.last_click_btn.setText("Copy to Blast");
        }
        Button b = (Button) event.getSource();
        b.setText("Copied");
        contr.cluster_logo_contr.last_click_btn = b;
    }

    void set_input_path(String s) {
        inputPath = s;
    }

    public void set_id(int n) {
        id = n;
        cluster_id.setText(String.valueOf(id));
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
        return "left area\n" +
                reference_seq.substring(0, area_size) +
                "\npqs\n" +
                reference_seq.substring(area_size, reference_seq.length() - area_size) +
                "\nright area\n" +
                reference_seq.substring(reference_seq.length() - area_size);
    }

    public void set_area_size(int area_size) {
        this.area_size = area_size;
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
