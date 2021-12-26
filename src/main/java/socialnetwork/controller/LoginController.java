package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import socialnetwork.HelloApplication;
import socialnetwork.domain.UserCredentials;
import socialnetwork.service.Service;

public class LoginController {
    private Service service;

    @FXML
    Button loginButton;

    @FXML
    TextField textFieldEmail;

    @FXML
    PasswordField textFieldPassword;

    @FXML
    Label warnLabel;

    @FXML
    Button registerButton;

    @FXML
    public void initialize() {
    }

    public void onLoginButtonClick() {
        String email = textFieldEmail.getText();
        String password = textFieldPassword.getText();
        Long userId = service.login(new UserCredentials(email, password));
        if (userId == null) {
            warnLabel.setVisible(true);
            return;
        }
        HelloApplication.changeSceneToMainWindow(service, userId);
    }

    public void onRegisterButtonClick() {
        HelloApplication.changeSceneToRegister(service);
    }

    public void setService(Service service) {
        this.service = service;
    }
}
