package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import socialnetwork.App;
import socialnetwork.Util.controller.NotificationService;
import socialnetwork.service.Service;

public class ReportsController {
    Service service;
    NotificationService notificationService;
    Long userId;

    @FXML
    Button notificationsButton;

    @FXML
    Button homeButton;

    @FXML
    Button logoutButton;

    @FXML
    Button eventsButton;

    @FXML
    ImageView notificationButtonImage;

    @FXML
    DatePicker startDate;

    @FXML
    DatePicker endDate;

    @FXML
    Button previewR1Button;

    @FXML
    Button previewR2Button;

    @FXML
    Button exportR1Button;

    @FXML
    Button exportR2Button;

    @FXML
    Label chooseFriendLabel;

    @FXML
    ListView friendsList;

    @FXML
    ListView newMessagesList;

    @FXML
    Label startLabel;

    @FXML
    Label noMessagesLabel;

    @FXML
    ImageView noMessagesImage;

    @FXML
    Label newFriendsLabel;

    @FXML
    ListView newFriendsList;

    @FXML
    Label receivedMessagesLabel;

    @FXML
    ListView friendMessagesList;

    @FXML
    ImageView noFriendsImage;

    @FXML
    Label noFriendsLabel;

    @FXML
    Label nowLabel;

    @FXML
    public void initialize() {

    }

    public void initData(Service service, Long userId) {
        this.service = service;
        this.userId = userId;
        notificationService = new NotificationService(service, userId, notificationsButton,
                notificationButtonImage, String.valueOf(App.class.getResource("images/notificationsImage.png")),
                String.valueOf(App.class.getResource("images/activeNotifications.png")));
        notificationService.setPeriod(Duration.seconds(5));
        notificationService.start();
    }

    public void onLogoutButtonClick() {
        App.changeSceneToLogin(service);
    }

    public void onHomeButtonClick() {
        App.changeSceneToMainWindow(service, userId);
    }

    public void onNotificationsButtonClick() {
        App.changeSceneToMainWindow(service, userId);
    }

    public void onPreviewR1ButtonClick(){

    }

    public void onPreviewR2ButtonClick(){

    }

    public void onExportR1ButtonClick(){

    }

    public void onExportR2ButtonClick(){

    }

    public void onEventsButtonClick(){App.changeSceneToEventsWindow(service, userId);}

    public void onStartClick(){App.changeSceneToMessagesWindow(service, userId);}

    public void onNowClick(){App.changeSceneToAddFriendsWindow(service, userId);}
}
