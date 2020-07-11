package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Timotej Sujan
 */
public class blast_local_info_controller implements Initializable {
    @FXML
    private Text manual;

    private void init() {
        manual.setText("Programs sends the given seqnce to NCBI Blast program.");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        init();
    }
}
