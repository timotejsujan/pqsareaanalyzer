package home;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.Objects;

public class Main extends Application {
    private double x, y;
    public static String jar_folder_path;
    private static String OS = System.getProperty("os.name").toLowerCase();

    @Override
    public void start(Stage primaryStage) throws Exception {
        //jar_folder_path = System.getProperty("java.home"); //"src";
        //jar_folder_path += "/resources";
        jar_folder_path = new File(Main.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getPath();

        int last_slash = jar_folder_path.lastIndexOf('/');
        if (last_slash == -1){
            last_slash = jar_folder_path.lastIndexOf('\\');
        }
        jar_folder_path =  jar_folder_path.substring(0,last_slash)+ "/resources";


        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("Home.fxml")));

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

        return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0 );

    }


    public static void main(String[] args) {
        launch(args);
    }
}
