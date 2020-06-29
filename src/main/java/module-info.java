module hellofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.desktop;
    requires java.net.http;

    opens home to javafx.fxml;
    exports home;
}