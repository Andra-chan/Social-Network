package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import socialnetwork.App;
import socialnetwork.service.Service;

import static socialnetwork.Util.imageHelper.Helpers.setProfileImage;

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
    AnchorPane addFriendsPane;

    @FXML
    AnchorPane messagesPane;

    @FXML
    AnchorPane settingsPane;

    @FXML
    Button logoutButton;

    @FXML
    Button notificationsButton;

    @FXML
    Button eventsButton;

    @FXML
    Label nowLabel;

    @FXML
    Label noUpcomingEventsLabel;

    @FXML
    ImageView noUpcomingEventsImage;

    @FXML
    Label upcomingEventsLabel;

    @FXML
    ListView upcomingEventsList;

    @FXML
    public void initialize(){
    }



    /**
     * Initialize the data using the service and the logged-in user.
     * @param service the Service
     * @param userId logged-in users' id
     */
    public void initData(Service service, Long userId) {
        this.service = service;
        this.userId=userId;
        var user = service.getUser(userId);
        usernameLabel.setText(user.getFirstName() + " " + user.getLastName());
        setProfileImage(user, userImageView);

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
     * When the user clicks on the add friendsbutton, switch the scene
     */
    public void onMenuAddFriendsClick(){
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
    public void onMenuSettingsClick(){
        App.changeSceneToSettingsWindow(service, userId);
    }

    /**
     * When the user clicks on the logout button, switch the scene
     */
    public void onLogoutButtonClick(){
        App.changeSceneToLogin(service);
    }

    public void onEventsButtonClick(){
        App.changeSceneToEventsWindow(service, userId);
    }

    public void onNotificationsButtonClick(){
        App.changeSceneToMainWindow(service, userId);
    }

    public void onNowClick(){
        App.changeSceneToEventsWindow(service, userId);
    }
}
