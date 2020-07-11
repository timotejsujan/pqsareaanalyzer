package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Timotej Sujan
 */
public class pqsfinder_info_controller implements Initializable {

    public void open_doc() throws IOException {
        Desktop desktop = Desktop.getDesktop();
        File f = new File(Main.jar_folder_path + "/documentation/pqsfinder.pdf");
        desktop.open(f);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
