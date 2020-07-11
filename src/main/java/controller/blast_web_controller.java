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

    private blast_web blastAPI;

    public void send() throws Exception {
        blastAPI.set_database(database.getValue());
        blastAPI.set_megablast(megablast.getValue());

        blastAPI.send(sequence.getText());
        time.setText("The results with id " + blastAPI.id + " will be available in estimated " + blastAPI.time + " seconds.");
        link.setText("Show results");
        link.setVisible(true);
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
