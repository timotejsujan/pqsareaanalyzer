package controller;

import javafx.fxml.Initializable;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Timotej Sujan
 */
public class blast_web_info_controller implements Initializable {

    public void open_doc() throws IOException {
        Desktop desktop = Desktop.getDesktop();
        File f = new File(Main.jar_folder_path + "/documentation/blast_web.html");
        desktop.open(f);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
