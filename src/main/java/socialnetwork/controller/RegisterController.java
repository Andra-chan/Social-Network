package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import socialnetwork.HelloApplication;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.Service;

public class RegisterController {
    Service service;

    @FXML
    TextField textFieldFirstName;

    @FXML
    TextField textFieldLastName;

    @FXML
    TextField textFieldEmail;

    @FXML
    PasswordField textFieldPassword;

    @FXML
    Button registerButton;

    @FXML
    Label errorLabel;

    @FXML
    public void initialize() {
    }

    public void onRegisterButtonPress() {
        String firstName = textFieldFirstName.getText();
        String lastName = textFieldLastName.getText();
        String email = textFieldEmail.getText();
        String password = textFieldPassword.getText();
        try {
            var user = service.addUser(new Utilizator(firstName, lastName, email, password));
            if (user == null) {
                HelloApplication.changeSceneToLogin(service);
            }
        } catch (Exception ex) {
            errorLabel.setText(ex.getMessage());
        }

    }

    public void setService(Service service) {
        this.service = service;
    }

}
