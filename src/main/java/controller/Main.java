package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.text.Font;
import model.config;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

public class Main extends Application {
    private double x, y;
    public static String jar_folder_path;
    public static config config;
    private static final String OS = System.getProperty("os.name").toLowerCase();
    public static Stage primary_stage;

    @Override
    public void start(Stage primaryStage) throws Exception {

        primary_stage = primaryStage;
        //jar_folder_path = System.getProperty("java.home"); //"src";
        //jar_folder_path += "/resources";
        jar_folder_path = new File(Main.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getPath();

        int last_slash = jar_folder_path.lastIndexOf('/');
        if (last_slash == -1) {
            last_slash = jar_folder_path.lastIndexOf('\\');
        }
        jar_folder_path = Paths.get(jar_folder_path.substring(0, last_slash), "resources").toString();

        try {
            init_config();
        } catch (Exception e){

        }

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("base.fxml")));

        if (isUnix()){
            root.setStyle(root.getStyle()+"; -fx-font-size: 12");
        }

        primaryStage.setScene(new Scene(root));
        //set stage borderless
        primaryStage.initStyle(StageStyle.UNDECORATED);

        //drag it here
        root.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {

            primaryStage.setX(event.getScreenX() - x);
            primaryStage.setY(event.getScreenY() - y);

        });
        primaryStage.show();
    }

    public static boolean isWindows() {

        return (OS.contains("win"));

    }

    public static boolean isMac() {

        return (OS.contains("mac"));

    }

    public static boolean isUnix() {

        return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0);

    }

    public void init_config() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File config_f = new File(Paths.get(Main.jar_folder_path, "config.json").toString());
        config = new config();
        config = mapper.readValue(config_f, config.class);
    }

    public static void save_config() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(
                new FileOutputStream(Paths.get(Main.jar_folder_path, "config.json").toString()), config);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
