module com.example.banterbox {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.example.banterbox to javafx.fxml;
    exports com.example.banterbox;
}