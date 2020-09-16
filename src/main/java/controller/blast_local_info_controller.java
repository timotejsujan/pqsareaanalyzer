package controller;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Timotej Sujan
 */
public class blast_local_info_controller extends info_helper implements Initializable {

    public void open_doc() {
        open_doc("blast_local.pdf");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
