package controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class base_controller extends Application implements Initializable {
    @FXML
    public StackPane main_pnl;
    @FXML
    private Button btn_exit;
    @FXML
    private Pane pqsfinder_pnl, pqsfinder_info_pnl, pqsareas_pnl, pqsareas_info_pnl, cdhit_pnl, cdhit_info_pnl,
            cdhit2D_pnl, cdhit2D_info_pnl, clusters_logo_pnl, clusters_logo_info_pnl, blastapi_pnl, blastapi_info_pnl,
            settings_pnl, blast_local_pnl, blast_local_info_pnl, clusters_compare_pnl, clusters_compare_info_pnl;

    public pqsfinder_controller pqsfinder_contr;
    public pqsareas_controller pqsareas_contr;
    public cdhit_controller cdhit_contr;
    public cdhit2D_controller cdhit2D_contr;
    public clusters_logo_controller clusters_logo_contr;
    public blast_web_controller blastapi_contr;
    public settings_controller settings_contr;
    public blast_local_controller blast_local_contr;
    public blast_local_info_controller blast_local_info_contr;
    public clusters_compare_controller clusters_compare_contr;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("pqsfinder.fxml"));
            pqsfinder_pnl = loader.load();
            pqsfinder_contr = loader.getController();
            pqsfinder_contr.contr = this;
            main_pnl.getChildren().add(pqsfinder_pnl);

            loader = new FXMLLoader(getClass().getClassLoader().getResource("pqsfinder_info.fxml"));
            pqsfinder_info_pnl = loader.load();
            main_pnl.getChildren().add(pqsfinder_info_pnl);

            loader = new FXMLLoader(getClass().getClassLoader().getResource("pqsareas.fxml"));
            pqsareas_pnl = loader.load();
            pqsareas_contr = loader.getController();
            pqsareas_contr.contr = this;
            main_pnl.getChildren().add(pqsareas_pnl);

            loader = new FXMLLoader(getClass().getClassLoader().getResource("pqsareas_info.fxml"));
            pqsareas_info_pnl = loader.load();
            main_pnl.getChildren().add(pqsareas_info_pnl);

            loader = new FXMLLoader(getClass().getClassLoader().getResource("cdhit.fxml"));
            cdhit_pnl = loader.load();
            cdhit_contr = loader.getController();
            cdhit_contr.contr = this;
            main_pnl.getChildren().add(cdhit_pnl);

            loader = new FXMLLoader(getClass().getClassLoader().getResource("cdhit_info.fxml"));
            cdhit_info_pnl = loader.load();
            main_pnl.getChildren().add(cdhit_info_pnl);

            loader = new FXMLLoader(getClass().getClassLoader().getResource("cdhit2D.fxml"));
            cdhit2D_pnl = loader.load();
            cdhit2D_contr = loader.getController();
            cdhit2D_contr.contr = this;
            main_pnl.getChildren().add(cdhit2D_pnl);

            loader = new FXMLLoader(getClass().getClassLoader().getResource("cdhit2D_info.fxml"));
            cdhit2D_info_pnl = loader.load();
            main_pnl.getChildren().add(cdhit2D_info_pnl);

            loader = new FXMLLoader(getClass().getClassLoader().getResource("clusters_logo.fxml"));
            clusters_logo_pnl = loader.load();
            clusters_logo_contr = loader.getController();
            clusters_logo_contr.contr = this;
            main_pnl.getChildren().add(clusters_logo_pnl);

            loader = new FXMLLoader(getClass().getClassLoader().getResource("clusters_logo_info.fxml"));
            clusters_logo_info_pnl = loader.load();
            main_pnl.getChildren().add(clusters_logo_info_pnl);

            loader = new FXMLLoader(getClass().getClassLoader().getResource("blast_web.fxml"));
            blastapi_pnl = loader.load();
            blastapi_contr = loader.getController();
            main_pnl.getChildren().add(blastapi_pnl);

            loader = new FXMLLoader(getClass().getClassLoader().getResource("blast_web_info.fxml"));
            blastapi_info_pnl = loader.load();
            main_pnl.getChildren().add(blastapi_info_pnl);

            loader = new FXMLLoader(getClass().getClassLoader().getResource("blast_local.fxml"));
            blast_local_pnl = loader.load();
            blast_local_contr = loader.getController();
            //blast_local_contr.contr = this;
            main_pnl.getChildren().add(blast_local_pnl);

            loader = new FXMLLoader(getClass().getClassLoader().getResource("blast_local_info.fxml"));
            blast_local_info_pnl = loader.load();
            blast_local_info_contr = loader.getController();
            main_pnl.getChildren().add(blast_local_info_pnl);

            loader = new FXMLLoader(getClass().getClassLoader().getResource("clusters_compare.fxml"));
            clusters_compare_pnl = loader.load();
            clusters_compare_contr = loader.getController();
            main_pnl.getChildren().add(clusters_compare_pnl);

            loader = new FXMLLoader(getClass().getClassLoader().getResource("settings.fxml"));
            settings_pnl = loader.load();
            settings_contr = loader.getController();
            settings_contr.contr = this;
            main_pnl.getChildren().add(settings_pnl);

        } catch (IOException e) {
            e.printStackTrace();
        }
        btn_exit.setOnAction(actionEvent -> Platform.exit());

    }

    @FXML
    private Button pqsfinder_btn, pqsfinder_info, pqsareas_btn, pqsareas_info, cdhit2_btn, cdhit2_info,
                     cdhit_btn, cdhit_info, blast_web_btn, blast_web_info, clusters_btn, clusters_info,
                    settings_btn, blast_local_btn, blast_local_info, clusters_compare_btn;

    public void handleClicks(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source == clusters_btn) clusters_logo_pnl.toFront();
        else if (source == clusters_info) clusters_logo_info_pnl.toFront();
        else if (source == pqsareas_btn) pqsareas_pnl.toFront();
        else if (source == pqsareas_info) pqsareas_info_pnl.toFront();
        else if (source == pqsfinder_btn) pqsfinder_pnl.toFront();
        else if (source == pqsfinder_info) pqsfinder_info_pnl.toFront();
        else if (source == cdhit_btn) cdhit_pnl.toFront();
        else if (source == cdhit_info) cdhit_info_pnl.toFront();
        else if (source == cdhit2_btn) cdhit2D_pnl.toFront();
        else if (source == cdhit2_info) cdhit2D_info_pnl.toFront();
        else if (source == blast_web_btn) blastapi_pnl.toFront();
        else if (source == blast_web_info) blastapi_info_pnl.toFront();
        else if (source == settings_btn) settings_pnl.toFront();
        else if (source == blast_local_btn) blast_local_pnl.toFront();
        else if (source == blast_local_info) blast_local_info_pnl.toFront();
        else if (source == clusters_compare_btn) clusters_compare_pnl.toFront();
    }

    @Override
    public void start(Stage stage) {

    }




}
