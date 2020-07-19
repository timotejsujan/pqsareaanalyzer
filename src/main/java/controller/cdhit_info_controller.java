package controller;

import javafx.fxml.Initializable;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * @author Timotej Sujan
 */
public class cdhit_info_controller implements Initializable {

    public void open_doc() {
        if (Desktop.isDesktopSupported()) {
            try {
                File f = new File(Paths.get(Main.jar_folder_path , "documentation", "cdhit.pdf").toString());
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
