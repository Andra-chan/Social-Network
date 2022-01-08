module socialnetwork {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires spring.security.crypto;
    requires commons.validator;

    opens socialnetwork to javafx.graphics, javafx.fxml, javafx.base;
    opens socialnetwork.domain to javafx.graphics, javafx.fxml, javafx.base;
    opens socialnetwork.controller to javafx.base, javafx.fxml, javafx.graphics;
    opens socialnetwork.repository to javafx.base, javafx.fxml, javafx.graphics, javafx.swing;
}
