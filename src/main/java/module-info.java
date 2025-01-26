module com.example.konyvtar {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;

    opens com.example.konyvtar to javafx.fxml;
    exports com.example.konyvtar;
}