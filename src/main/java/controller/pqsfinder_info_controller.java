package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * @author Timotej Sujan
 */
public class pqsfinder_info_controller implements Initializable {


    public void open_doc() {
        if (Desktop.isDesktopSupported()) {
            try {
                File f = new File(Paths.get(Main.jar_folder_path, "documentation", "pqsfinder.pdf").toString());
                Desktop.getDesktop().open(f);
            } catch (IOException ex) {
                // no application registered for PDFs
            }
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
