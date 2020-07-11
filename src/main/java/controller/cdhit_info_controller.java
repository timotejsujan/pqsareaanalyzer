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
public class cdhit_info_controller implements Initializable {
    @FXML
    private Text manual;

    public void open_doc() throws IOException {
        Desktop desktop = Desktop.getDesktop();
        File f = new File(Main.jar_folder_path + "/documentation/cdhit.pdf");
        desktop.open(f);
    }

    public void init() {
        manual.setText(
                "CD-HIT-EST clusters a nucleotide dataset into clusters that meet\na user-defined similarity threshold, " +
                        "usually a sequence identity.\nThe input is a DNA/RNA dataset in fasta format and the output is\n" +
                        "a fasta file of sequences in a list of sorted clusters.\n" +
                        "Since eukaryotic genes usually have long introns, which cause long gaps, it is difficult to make " +
                        "\nfull-length alignments for these genes. \n" +
                        "So, CD-HIT-EST is good for non-intron containing sequences like EST. \n\n" +
                        "Program will run from several seconds to a few hours depending on \n" +
                        "a size and structure of the input file and given parameters.\n\n" +
                        "You can read complete documentation after pressing the button \"Doc\"\nor in the folder \"Documentation\" in the root of the application.");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        init();
    }
}
