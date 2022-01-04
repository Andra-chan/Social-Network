package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import socialnetwork.App;
import socialnetwork.service.Service;

public class MainPageController {
    Service service;
    Long userId;

    @FXML
    Label usernameLabel;

    @FXML
    ImageView userImageView;

    @FXML
    AnchorPane friendsPane;

    @FXML
    AnchorPane friendRequestsPane;

    @FXML
    AnchorPane messagesPane;

    @FXML
    AnchorPane settingsPane;

    @FXML
    public void initialize(){
    }
    

    public void initData(Service service, Long userId) {
        this.service = service;
        this.userId=userId;
        var user = service.getUser(userId);
        usernameLabel.setText(user.getFirstName() + " " + user.getLastName());
    }

    public void onMenuFriendsClick() {
        App.changeSceneToFriendsWindow(service, userId);
    }

    public void onMenuFriendRequestsClick() {
        App.changeSceneToFriendRequestsWindow(service, userId);
    }

    public void onMenuMessagesClick() {
        App.changeSceneToMessagesWindow(service, userId);
    }

    public void onMenuSettingsClick(){
        App.changeSceneToSettingsWindow(service, userId);
    }
}
