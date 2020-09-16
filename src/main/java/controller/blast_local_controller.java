package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.blast_local;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

/**
 * @author Timotej Sujan
 */
public class blast_local_controller extends helper implements Initializable {

    public Button output_dir_btn;
    public Button database_btn;
    @FXML TextField database, params;
    @FXML TextArea sequence;

    model.blast_local blast_local;

    /***
     * Starts the process.
     * @throws IOException
     */
    public void start() throws IOException {
        blast_local.set_output_name(output_name.getText());
        blast_local.set_parameters(params.getText());

        if (!blast_local.is_valid()) {
            blast_local.print_stream.println("values are NOT valid, something is missing!");
            return;
        }

        // Makes a file from sequence to use in blastn program
        Files.write(Paths.get(blast_local.output_path, "$$$temp_query.txt"), sequence.getText().getBytes());
        // Pass the path for later deletion of file
        blast_local.set_input_path(Paths.get(blast_local.output_path, "$$$temp_query.txt").toString());

        exec_service = Executors.newSingleThreadExecutor();

        exec_service.execute(() -> {
            try {
                blast_local.start();
                exec_service.shutdownNow();
            } catch (Exception e) {
                blast_local.print_stream.println(e.toString());
                exec_service.shutdownNow();
                e.printStackTrace();
            }
        });
        switch_disabled();
        java.util.Date date = new java.util.Date();
        blast_local.print_stream.println(date.toString() + " the process has started");
        Runnable r = () -> {
            if (exec_service.isShutdown()) {
                switch_disabled();
                timeline.stop();
            }
        };
        timeline = create_timeline(5, r);
        timeline.play();
    }

    public void stop() {
        java.util.Date date = new java.util.Date();
        blast_local.print_stream.println(date.toString() + " the process has been stopped externally");
        exec_service.shutdownNow();
    }

    public void set_output_dir() throws IOException {
        File file = dir_chooser.showDialog(Main.primary_stage);
        if (file != null) {
            output_dir.setText(".../"+file.getName());
            blast_local.set_output_dir(file.getAbsolutePath());
            Main.config.output_dir_name = file.getName();
            Main.config.output_dir = file.getAbsolutePath();
            Main.save_config();
        }
    }

    // TODO
    public void set_db_path() {
        File file = file_chooser.showOpenDialog(Main.primary_stage);
        if (file != null) {
            //if (file.getName().lastIndexOf(".") != -1) {
                database.setText(".../" + file.getName().substring(0, file.getName().lastIndexOf(".")));
            //}
            blast_local.set_database_path(file.getAbsolutePath());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        blast_local = new blast_local(area);
        init_config();
    }

    private void switch_disabled(){
        start_btn.setDisable(!start_btn.isDisable());
        stop_btn.setDisable(!stop_btn.isDisable());
        output_dir.setDisable(!output_dir.isDisable());
        output_dir_btn.setDisable(!output_dir_btn.isDisable());
        database.setDisable(!database.isDisable());
        database_btn.setDisable(!database_btn.isDisable());
    }

    /***
     * Initializes config
     */
    public void init_config() {
        // if the program is running, return
        if (exec_service != null && !exec_service.isShutdown()) return;

        // sets the output directory
        if (Main.config.output_dir_name != null && Main.config.output_dir != null) {
            output_dir.setText(".../"+Main.config.output_dir_name);
            blast_local.output_path = Main.config.output_dir;
        }

        // sets the program path
        if (Main.config.blast_path != null)
            blast_local.set_program_path(Main.config.blast_path);
    }
}
