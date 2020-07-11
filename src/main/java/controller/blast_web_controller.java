package controller;

import javafx.scene.control.*;
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
    @FXML
    private Button link;
    @FXML
    public TextArea sequence;
    @FXML
    private Label time;
    @FXML
    public ChoiceBox<String> database, megablast;
    @FXML TextField filter, format_type, expect, nucl_reward, nucl_penalty, gapcosts, matrix,
            hitlist_size, descriptions, alignments, ncbi_gi, threshold, word_size, composition_based_statistics,
            num_threads;
    

    private blast_web blastAPI;

    private void set_parameters(){
        blastAPI.filter = filter.getText();
        blastAPI.format_type = format_type.getText();
        blastAPI.expect = expect.getText();
        blastAPI.nucl_reward = nucl_reward.getText();
        blastAPI.nucl_penalty = nucl_penalty.getText();
        blastAPI.gapcosts = gapcosts.getText();
        blastAPI.matrix = matrix.getText();
        blastAPI.hitlist_size = hitlist_size.getText();
        blastAPI.descriptions = descriptions.getText();
        blastAPI.alignments = alignments.getText();
        blastAPI.ncbi_gi = ncbi_gi.getText();
        blastAPI.threshold = threshold.getText();
        blastAPI.word_size = word_size.getText();
        blastAPI.composition_based_statistics = composition_based_statistics.getText();
        blastAPI.num_threads = num_threads.getText();
    }

    public void send() throws Exception {
        set_parameters();

        blastAPI.set_database(database.getValue());
        blastAPI.set_megablast(megablast.getValue());
        blastAPI.send(sequence.getText());
        time.setText("The results with id " + blastAPI.id + " will be available in estimated " + blastAPI.time + " seconds.");
        link.setDisable(false);
    }

    public void open_in_browser() {
        getHostServices().showDocument("https://blast.ncbi.nlm.nih.gov/Blast.cgi?CMD=Get&RID=" + blastAPI.id);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        blastAPI = new blast_web();
        database.getItems().setAll("nt", "refseq_rna", "pdbnt");
        megablast.getItems().setAll("on", "off");
    }

    @Override
    public void start(Stage stage) {

    }
}
