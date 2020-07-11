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
    @FXML
    private Text manual;

    public void open_doc() throws IOException {
        Desktop desktop = Desktop.getDesktop();
        File f = new File(Main.jar_folder_path + "/documentation/pqsfinder.pdf");
        desktop.open(f);
    }

    private void pqsfinderInit() {
        manual.setText(
                "Pqsfinder is a program for detecting DNA sequence patterns\nthat are likely to fold into an intramolecular G-quadruplex.\n\n" +
                        "After setting the input path to a genom and setting parameters,\nyou will get a file of positions of quadruplexes,\n" +
                        "which you can use in another programs in this application.\n\n" +
                        "Program will run from several minutes to a few hours depending on a size and structure of the genom.\n\n" +
                        "If you hover over the parameter's field, you will get additional information for the concrete parameter.\n\n" +
                        "You can read complete documentation after pressing the button \"Doc\"\nor in the folder \"Documentation\" in the root of the application.");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pqsfinderInit();
    }
}
