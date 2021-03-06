package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import socialnetwork.App;
import socialnetwork.Util.controller.NotificationService;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.Service;

import java.io.File;

import static socialnetwork.Util.imageHelper.Helpers.setProfileImage;

public class SettingsController {
    Service service;
    Long userId;
    Utilizator currentUser;
    NotificationService notificationService;

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
    Button notificationsButton;

    @FXML
    Button eventsButton;

    @FXML
    ImageView notificationButtonImage;

    @FXML
    Button reportsButton;

    @FXML
    public void initialize() {
        warningLabel.setVisible(false);
    }

    public void initData(Service service, Long userId) {
        this.service = service;
        this.userId = userId;
        this.currentUser = service.getUser(userId);
        update();
        notificationService = new NotificationService(service, userId, notificationsButton,
                notificationButtonImage, String.valueOf(App.class.getResource("images/notificationsImage.png")),
                String.valueOf(App.class.getResource("images/activeNotifications.png")));
        notificationService.setPeriod(Duration.seconds(5));
        notificationService.start();
    }

    private void update() {
        setProfileImage(currentUser, profileImage);
        firstNameField.setText(currentUser.getFirstName());
        lastNameField.setText(currentUser.getLastName());
    }


    public void onChooseImageButtonClick() {
        FileChooser choose = new FileChooser();
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG", "*.JPEG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        choose.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
        File file = choose.showOpenDialog(null);
        if (file == null) {
            return;
        }
        try {
            currentUser.setImagePath(file.toURI().toURL().toExternalForm());
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
            newUser.setImagePath(currentUser.getImagePath());
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

    public void onEventsButtonClick(){
        App.changeSceneToEventsWindow(service, userId);
    }

    public void onNotificationsButtonClick(){
        App.changeSceneToMainWindow(service, userId);
    }

    public void onReportsButtonClick(){
        App.changeSceneToReportsWindow(service, userId);
    }
}
