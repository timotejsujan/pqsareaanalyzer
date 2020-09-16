package controller;

import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Window;
import model.blast_web;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Timotej Sujan
 */
public class blast_web_controller extends Application implements Initializable {
    @FXML private Button link;
    @FXML public TextArea sequence;
    @FXML private TextField id, time;
    @FXML public ChoiceBox<String> database, megablast;
    @FXML TextField filter, format_type, expect, nucl_reward, nucl_penalty, gapcosts, matrix,
            hitlist_size, descriptions, alignments, ncbi_gi, threshold, word_size, composition_based_statistics,
            num_threads;
    

    private blast_web blast_web;

    private void set_parameters(){
        blast_web.filter = filter.getText();
        blast_web.format_type = format_type.getText();
        blast_web.expect = expect.getText();
        blast_web.nucl_reward = nucl_reward.getText();
        blast_web.nucl_penalty = nucl_penalty.getText();
        blast_web.gapcosts = gapcosts.getText();
        blast_web.matrix = matrix.getText();
        blast_web.hitlist_size = hitlist_size.getText();
        blast_web.descriptions = descriptions.getText();
        blast_web.alignments = alignments.getText();
        blast_web.ncbi_gi = ncbi_gi.getText();
        blast_web.threshold = threshold.getText();
        blast_web.word_size = word_size.getText();
        blast_web.composition_based_statistics = composition_based_statistics.getText();
        blast_web.num_threads = num_threads.getText();
    }

    public void send() throws Exception {
        set_parameters();

        blast_web.set_database(database.getValue());
        blast_web.set_megablast(megablast.getValue());

        blast_web.send(sequence.getText());
        id.setText(blast_web.id);
        time.setText(blast_web.time);
        link.setDisable(false);
    }

    public void open_in_browser() {
        getHostServices().showDocument("https://blast.ncbi.nlm.nih.gov/Blast.cgi?CMD=Get&RID=" + blast_web.id);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        blast_web = new blast_web();
        database.getItems().setAll("nt", "refseq_rna", "pdbnt");
        megablast.getItems().setAll("on", "off");
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
