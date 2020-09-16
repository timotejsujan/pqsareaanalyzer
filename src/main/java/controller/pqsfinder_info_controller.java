package controller;

import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Timotej Sujan
 */
public class pqsfinder_info_controller extends info_helper implements Initializable {


    public void open_doc() {
        open_doc("pqsfinder.pdf");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
