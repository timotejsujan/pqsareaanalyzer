package home;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Timotej Sujan
 */
public class pqsareas_info_controller implements Initializable {
    @FXML
    private Text manual;

    private void init() {
        manual.setText(
                "PQSareas takes the genom and PQS positions and outputs two files:\n" +
                        "a fasta file of PQS areas and a fasta file of PQS.\n\n" +
                        "Program will run from several seconds to a few minutes depending on \n" +
                        "a size and structure of the input file and given parameters.");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        init();
    }
}
