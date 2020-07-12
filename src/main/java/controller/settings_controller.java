package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Timotej Sujan
 */
public class settings_controller implements Initializable {
    private final FileChooser file_chooser = new FileChooser();
    private final DirectoryChooser dir_chooser = new DirectoryChooser();
    public base_controller contr;

    @FXML
    TextField output_dir, cdhit_path, cdhit2D_path, blast_path;


    public void set_output_dir() throws IOException {
        File file = dir_chooser.showDialog(Main.primary_stage);
        if (file != null) {
            output_dir.setText(".../"+file.getName());

            Main.config.output_dir = file.getAbsolutePath();
            Main.config.output_dir_name = file.getName();

            contr.pqsfinder_contr.init_config();
            contr.pqsareas_contr.init_config();
            contr.cdhit_contr.init_config();
            contr.cdhit2D_contr.init_config();
            contr.blast_local_contr.init_config();

            Main.save_config();
        }
    }

    public void set_cdhit_path() throws IOException {
        File file = file_chooser.showOpenDialog(Main.primary_stage);
        if (file != null) {
            file.setExecutable(true);
            if (!file.canExecute()){
                showAlert(Alert.AlertType.WARNING, Main.primary_stage, "Permission", "You have to set execute permissions for this file!");
            }
            cdhit_path.setText(".../"+file.getName());
            contr.cdhit_contr.cdhit.set_program_path(file.getAbsolutePath());
            Main.config.cdhit_path = file.getAbsolutePath();
            Main.save_config();
        }
    }

    public void set_cdhit2D_path() throws IOException {
        File file = file_chooser.showOpenDialog(Main.primary_stage);
        if (file != null) {
            file.setExecutable(true);
            if (!file.canExecute()){
                showAlert(Alert.AlertType.WARNING, Main.primary_stage, "Permission", "You have to set execute permissions for this file!");
            }
            cdhit2D_path.setText(".../"+file.getName());
            contr.cdhit2D_contr.cdhit2D.set_program_path(file.getAbsolutePath());
            Main.config.cdhit2D_path = file.getAbsolutePath();
            Main.save_config();
        }
    }

    public void set_blast_path() throws IOException {
        File file = file_chooser.showOpenDialog(Main.primary_stage);
        if (file != null) {
            file.setExecutable(true);
            if (!file.canExecute()){
                showAlert(Alert.AlertType.WARNING, Main.primary_stage, "Permission", "You have to set execute permissions for this file!");
            }
            blast_path.setText(".../"+file.getName());
            //contr.blastapi_contr.blastapi.set_program_path(file.getAbsolutePath());
            Main.config.blast_path = file.getAbsolutePath();
            Main.save_config();
        }
    }

    public void init_config(){
        //Rscript_path.setText(Main.config.Rscript_path);
        if (Main.config.output_dir != null && Main.config.output_dir_name != null)
            output_dir.setText(Main.config.output_dir);
        if (Main.config.cdhit_path != null)
            cdhit_path.setText(Main.config.cdhit_path);
        if (Main.config.cdhit2D_path != null)
            cdhit2D_path.setText(Main.config.cdhit2D_path);
        if (Main.config.blast_path != null)
            blast_path.setText(Main.config.blast_path);
    }

    public void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        init_config();
    }
}
