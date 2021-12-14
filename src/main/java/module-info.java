module socialnetwork {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens socialnetwork to javafx.graphics, javafx.fxml, javafx.base;
    opens socialnetwork.domain to javafx.graphics, javafx.fxml, javafx.base;
}
