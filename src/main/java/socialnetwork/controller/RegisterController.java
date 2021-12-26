package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
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

    /**
     * Initialize the controller. Add 'enter' key listeners to text fields.
     */
    @FXML
    public void initialize() {
        textFieldFirstName.setOnKeyPressed(event -> enterKeyPressed(event.getCode()));
        textFieldLastName.setOnKeyPressed(event -> enterKeyPressed(event.getCode()));
        textFieldEmail.setOnKeyPressed(event -> enterKeyPressed(event.getCode()));
        textFieldPassword.setOnKeyPressed(event -> enterKeyPressed(event.getCode()));
    }

    /**
     * Click handler for the register button
     *
     * When the register button is pressed, collect registration info from text
     * fields and attempt to register a new user.
     *
     * If a new user has been created, the window will switch from register window
     * to a login window, otherwise an error message is displayed.
     */
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

    /**
     * Handler for the enter key.
     * When the enter key is pressed, collect registration info and try to register
     * a new user.
     */
    private void enterKeyPressed(KeyCode keyCode) {
        if (keyCode == KeyCode.ENTER) {
            onRegisterButtonPress();
        }
    }

    /**
     * Initialize the service
     */
    public void setService(Service service) {
        this.service = service;
    }

}
