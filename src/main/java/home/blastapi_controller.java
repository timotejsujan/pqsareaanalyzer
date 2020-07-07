package home;

import external.blast_api;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Timotej Sujan
 */
public class blastapi_controller extends Application implements Initializable {
    @FXML
    private Button link;
    @FXML
    public TextField sequence;
    @FXML
    private Label time;
    @FXML
    public ChoiceBox<String> database, megablast;

    private blast_api blastAPI;

    public void send() throws Exception {
        set_database();
        set_megablast();
        blastAPI.send(sequence.getText());
        time.setText("The results with id " + blastAPI.id + " will be available in estimated " + blastAPI.time + " seconds.");
        link.setText("Show results");
        link.setVisible(true);
    }

    public void open_in_browser() {
        getHostServices().showDocument("https://blast.ncbi.nlm.nih.gov/Blast.cgi?CMD=Get&RID=" + blastAPI.id);
    }

    private void set_database() {
        blastAPI.set_database(database.getValue());
    }

    private void set_megablast() {
        blastAPI.set_megablast(megablast.getValue());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        blastAPI = new blast_api();
        database.getItems().setAll("nt", "refseq_rna", "pdbnt");
        megablast.getItems().setAll("on", "off");
    }

    @Override
    public void start(Stage stage) {

    }
}
