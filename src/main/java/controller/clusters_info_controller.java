package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Timotej Sujan
 */
public class clusters_info_controller implements Initializable {
    @FXML
    private Text manual;

    private void init() {
        manual.setText(
                "The program takes the list of clusters (output of CD-hit programs)\n" +
                        "and outputs a table of visualizable clusters."
        );
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        init();
    }
}
