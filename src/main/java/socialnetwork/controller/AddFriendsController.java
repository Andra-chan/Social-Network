package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import socialnetwork.App;
import socialnetwork.service.Service;

public class AddFriendsController {
    Service service;
    Long userId;

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
    public void initialize(){
    }

    public void initData(Service service, Long userId) {
        this.service=service;
        this.userId=userId;
    }

    public void onMenuFriendsClick() {
        App.changeSceneToFriendsWindow(service, userId);
    }

    public void onMenuFriendRequestsClick() {
        App.changeSceneToFriendRequestsWindow(service, userId);
    }

    public void onMenuAddFriendsClick(){
        App.changeSceneToAddFriendsWindow(service, userId);
    }

    public void onMenuMessagesClick() {
        App.changeSceneToMessagesWindow(service, userId);
    }

    public void onMenuSettingsClick(){
        App.changeSceneToSettingsWindow(service, userId);
    }

    public void onLogoutButtonClick(){
        App.changeSceneToLogin(service);
    }
}

