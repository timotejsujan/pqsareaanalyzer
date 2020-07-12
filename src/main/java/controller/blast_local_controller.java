package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

    @FXML
    TextField database, params;
    @FXML
    TextArea sequence;

    model.blast_local blast_local;

    public void start() throws IOException {
        blast_local.set_output_name(output_name.getText());
        blast_local.set_parameters(params.getText());

        Files.write(Paths.get(blast_local.output_path + "/" + "temp_query.txt"), sequence.getText().getBytes());

        blast_local.set_input_path(blast_local.output_path + "/" + "temp_query.txt");

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
        start_btn.setDisable(true);
        stop_btn.setDisable(false);
        java.util.Date date = new java.util.Date();
        blast_local.print_stream.println(date.toString() + " the process has started");
        Runnable r = () -> {
            if (exec_service.isShutdown()) {
                start_btn.setDisable(false);
                stop_btn.setDisable(true);
                timeline.stop();
            }
        };
        timeline = create_timeline(5, r);
        timeline.play();
    }

    public void stop() {
        if (blast_local.p.isAlive()) {
            blast_local.p.destroy();
        }
        java.util.Date date = new java.util.Date();
        blast_local.print_stream.println(date.toString() + " the process has been stopped externally");
        exec_service.shutdownNow();
        start_btn.setDisable(false);
        stop_btn.setDisable(true);
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

    public void set_db_path() {
        File file = file_chooser.showOpenDialog(Main.primary_stage);
        if (file != null) {
            database.setText(".../" + file.getName().substring(0, file.getName().lastIndexOf(".")));
            blast_local.set_database_path(file.getAbsolutePath());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        blast_local = new blast_local(area);
        init_config();
    }

    public void init_config() {
        if (Main.config.output_dir_name != null && Main.config.output_dir != null) {
            output_dir.setText(".../"+Main.config.output_dir_name);
            blast_local.output_path = Main.config.output_dir;
        }
        blast_local.set_program_path(Main.config.blast_path);
    }
}
