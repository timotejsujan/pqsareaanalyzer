package home;

import external.clusters_count;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author Timotej Sujan
 */
public class clusters_controller implements Initializable {
    private final FileChooser file_chooser = new FileChooser();

    @FXML
    private VBox items;
    @FXML
    public Button last_click_btn, export_btn;
    @FXML
    public TextField input_path, limit;
    @FXML
    private Label number_of_clusters;
    private external.clusters_count clusters_count;
    public blastapi_controller blastapi_contr;

    @FXML
    public void start() {
        set_limit();
        //clusters_count.loadCluster();
        Node[] nodes = new Node[Math.min(clusters_count.getLimit(), clusters_count.getLength())];
        /*
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Item.fxml"));
        Node node = (Node) loader.load();
        VisualizationController vc = loader.getController();
        vc.setInputPath(clustersCount.getInputPath());
         */
        items.getChildren().clear();
        for (int i = 0; i < nodes.length; i++) {
            try {

                final int j = i;
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Item.fxml"));
                //URL url = new File("src/main/resources/Item.fxml").toURI().toURL();
                //FXMLLoader loader = new FXMLLoader(url);
                nodes[j] = loader.load();
                visualization_controller vc = loader.getController();
                vc.set_input_path(clusters_count.getInputPath());
                vc.set_id(i);
                vc.set_size(clusters_count.getReferenceSeqs().get(i).size);
                vc.set_reference_sequence(clusters_count.getReferenceSeqs().get(i).reference_sequence);
                vc.set_controllers(this, blastapi_contr);

                nodes[i].setOnMouseEntered(event -> nodes[j].setStyle("-fx-background-color : #FFFFFF"));
                nodes[i].setOnMouseExited(event -> nodes[j].setStyle("-fx-background-color : #FFFFFF"));
                //nodes[j].setId(i.toString());
                items.getChildren().add(nodes[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void set_input_path() throws IOException {
        File file = file_chooser.showOpenDialog(null);
        if (file != null) {
            input_path.setText(file.getName());
            clusters_count.setInputPath(file.getAbsolutePath());
            clusters_count.load_cluster();
            number_of_clusters.setText("(max " + clusters_count.getLength() + ")");
            limit.setText(Integer.min(clusters_count.getLength(), 20) + "");
            export_btn.setDisable(false);
        }
    }

    private void set_limit() {
        if (limit.getText().isEmpty()) return;
        clusters_count.setLimit(Integer.parseInt(limit.getText()));
    }

    @FXML
    private void open_export_dialog() throws IOException {
        ButtonType export_button_type = new ButtonType("Export Table", ButtonBar.ButtonData.OK_DONE);
        TextInputDialog dialog = new TextInputDialog("1,2-4,5-9,10-19,20-49,50-99,100-up");
        dialog.setTitle("Enter cluster sizes columns");
        dialog.setHeaderText("Enter cluster sizes columns");
        //dialog.setContentText("Enter cluster sizes:");
        dialog.getDialogPane().setPrefWidth(300);
        dialog.getDialogPane().getButtonTypes().setAll(export_button_type, ButtonType.CANCEL);

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            FileChooser file_chooser = new FileChooser();
            file_chooser.setInitialFileName("clstr_table_"+input_path.getText());
            File file = file_chooser.showSaveDialog(null);
            if (file != null) {
                clusters_count.export_table(file, result.get());
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clusters_count = new clusters_count();
    }
}
