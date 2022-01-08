package socialnetwork.controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import socialnetwork.App;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class SettingsController {
    Service service;
    Long userId;
    Utilizator currentUser;

    @FXML
    AnchorPane friendsPane;

    @FXML
    AnchorPane friendRequestsPane;

    @FXML
    AnchorPane addFriendsPane;

    @FXML
    AnchorPane messagesPane;

    @FXML
    AnchorPane settingsPane;

    @FXML
    Button logoutButton;

    @FXML
    Button homeButton;

    @FXML
    ImageView profileImage;

    @FXML
    Button chooseImageButton;

    @FXML
    Button saveButton;

    @FXML
    TextField firstNameField;

    @FXML
    TextField lastNameField;

    @FXML
    Label warningLabel;

    @FXML
    public void initialize() {
        warningLabel.setVisible(false);
    }

    public void initData(Service service, Long userId) {
        this.service = service;
        this.userId = userId;
        this.currentUser = service.getUser(userId);
        update();
    }

    private void update() {
        if (currentUser.getImage_path() == null) {
            String path =  String.valueOf(App.class.getResource("images/defaultUserImage.png"));
            profileImage.setImage(new Image(path));
            profileImage.setBlendMode(BlendMode.DARKEN);
            currentUser.setImage_path(path);
        } else {
            profileImage.setImage(new Image(currentUser.getImage_path()));
        }
        firstNameField.setText(currentUser.getFirstName());
        lastNameField.setText(currentUser.getLastName());
    }


    public void onChooseImageButtonClick() {
        FileChooser choose = new FileChooser();
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG", "*.JPEG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        //choose.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
        File file = choose.showOpenDialog(null);
        try {
            currentUser.setImage_path(file.toURI().toURL().toExternalForm());
            update();
            profileImage.setBlendMode(BlendMode.SRC_OVER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onSaveButtonClick() {
        try {
            Utilizator newUser = new Utilizator(firstNameField.getText(), lastNameField.getText());
            newUser.setEmail(currentUser.getEmail());
            newUser.setImage_path(currentUser.getImage_path());
            newUser.setId(currentUser.getId());
            if (service.updateUser(newUser) == null) {
                currentUser = newUser;
                update();
            }
        } catch (Exception ex) {
            warningLabel.setText(ex.getMessage());
            warningLabel.setVisible(true);
        }
    }

    /**
     * When the user clicks on the friends button, switch the scene
     */
    public void onMenuFriendsClick() {
        App.changeSceneToFriendsWindow(service, userId);
    }

    /**
     * When the user clicks on the friend requests button, switch the scene
     */
    public void onMenuFriendRequestsClick() {
        App.changeSceneToFriendRequestsWindow(service, userId);
    }

    /**
     * When the user clicks on the add friends button, switch the scene
     */
    public void onMenuAddFriendsClick() {
        App.changeSceneToAddFriendsWindow(service, userId);
    }

    /**
     * When the user clicks on the messages button, switch the scene
     */
    public void onMenuMessagesClick() {
        App.changeSceneToMessagesWindow(service, userId);
    }

    /**
     * When the user clicks on the settings button, switch the scene
     */
    public void onMenuSettingsClick() {
        App.changeSceneToSettingsWindow(service, userId);
    }

    /**
     * When the user clicks on the logout button, switch the scene
     */
    public void onLogoutButtonClick() {
        App.changeSceneToLogin(service);
    }

    public void onHomeButtonClick() {
        App.changeSceneToMainWindow(service, userId);
    }
}
