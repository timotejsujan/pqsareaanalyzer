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
public class cdhit2D_info_controller implements Initializable {
    @FXML
    private Text manual;

    public void open_doc() throws IOException {
        Desktop desktop = Desktop.getDesktop();
        File f = new File(Main.jar_folder_path + "/documentation/cdhit.pdf");
        desktop.open(f);
    }

    public void init() {
        manual.setText(
                "CD-HIT-EST-2D compares 2 nucleotide datasets (db1, db2).\n" +
                        "It identifies the sequences in db2 that are similar to db1\n" +
                        "at a certain threshold. The input are two DNA/RNA datasets (db1, db2)\n" +
                        "in fasta format and the output are two files: a fasta file of sequences\n" +
                        "in db2 that are not similar to db1 and a text file that lists similar\n" +
                        "sequences between db1 & db2. For same reason as CD-HIT-EST, CD-HIT-EST-2D\n" +
                        "is good for non-intron containing sequences like EST.\n\n" +
                        "Program will run from several seconds to a few hours depending on \n" +
                        "a size and structure of the input file and given parameters.\n\n" +
                        "You can read complete documentation after pressing the button \"Doc\"\nor in the folder \"Documentation\" in the root of the application.");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        init();
    }
}
