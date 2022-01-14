package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import socialnetwork.App;
import socialnetwork.Util.controller.FriendListCell;
import socialnetwork.Util.controller.NotificationService;
import socialnetwork.domain.Friend;
import socialnetwork.domain.Message;
import socialnetwork.service.Service;
import socialnetwork.service.paging.PageableImplementation;

import java.util.List;

public class ReportsController {
    Service service;
    NotificationService notificationService;

    ObservableList<Friend> modelFriendships = FXCollections.observableArrayList();
    ObservableList<Message> modelMessages = FXCollections.observableArrayList();

    boolean isR2 = false;

    int currentFriendsPage = 0;
    final int friendsPageSize = 5;

    int currentMessagesPage = 0;
    final int messagesPageSize = 0;

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
    Label startLabel;

    @FXML
    Label noMessagesLabel;

    @FXML
    ImageView noMessagesImage;

    @FXML
    Label newFriendsLabel;

    @FXML
    ListView<Friend> friendsList;

    @FXML
    Label receivedMessagesLabel;

    @FXML
    ListView<Message> messagesList;

    @FXML
    ImageView noFriendsImage;

    @FXML
    Label noFriendsLabel;

    @FXML
    Label nowLabel;

    @FXML
    Button friendsNextPage;

    @FXML
    Button friendsPrevPage;

    @FXML
    Button messagesNextPage;

    @FXML
    Button messagesPrevPage;

    private void setR1Visibility(boolean state) {
        noFriendsImage.setVisible(state);
        noFriendsLabel.setVisible(state);
        nowLabel.setVisible(state);
        newFriendsLabel.setVisible(!state);
        friendsList.setVisible(!state);
        chooseFriendLabel.setVisible(state);

        messagesList.setVisible(!state);
        receivedMessagesLabel.setVisible(!state);
        noMessagesLabel.setVisible(state);
        noMessagesImage.setVisible(state);
        startLabel.setVisible(state);
    }

    private void stateR2Visibility(boolean state) {
        newFriendsLabel.setVisible(state);
        noFriendsImage.setVisible(state);
        noFriendsLabel.setVisible(state);
        nowLabel.setVisible(state);

        chooseFriendLabel.setVisible(!state);
        friendsList.setVisible(!state);
        receivedMessagesLabel.setVisible(!state);
        noMessagesLabel.setVisible(state);
        noMessagesImage.setVisible(state);
        startLabel.setVisible(state);
        messagesList.setVisible(!state);
    }

    @FXML
    public void initialize() {

    }

    public void initData(Service service, Long userId) {
        this.service = service;
        this.userId = userId;
        friendsList.setItems(modelFriendships);
        friendsList.setCellFactory(x -> new FriendListCell());
        notificationService = new NotificationService(service, userId, notificationsButton,
                notificationButtonImage, String.valueOf(App.class.getResource("images/notificationsImage.png")),
                String.valueOf(App.class.getResource("images/activeNotifications.png")));
        notificationService.setPeriod(Duration.seconds(5));
        notificationService.start();
    }

    public List<Friend> getFriends() {
        return service.getFriends(userId);
    }

    private void updateFriendsList() {
        modelFriendships.setAll(service.getFriendsFilteredPaged(userId, "", "", new PageableImplementation(currentFriendsPage, friendsPageSize)));
        noFriendsLabel.setVisible(modelFriendships.size() == 0);
        noFriendsImage.setVisible(modelFriendships.size() == 0);
        friendsList.setVisible(modelFriendships.size() != 0);
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

    public void onPreviewR1ButtonClick() {
        isR2 = false;
        currentMessagesPage = 0;
        currentFriendsPage = 0;
        setR1Visibility(false);
    }

    public void onPreviewR2ButtonClick() {
        isR2 = true;
        currentMessagesPage = 0;
        currentFriendsPage = 0;
        stateR2Visibility(false);
        updateFriendsList();
    }

    public void onExportR1ButtonClick() {

    }

    public void onExportR2ButtonClick() {

    }

    public void onEventsButtonClick() {
        App.changeSceneToEventsWindow(service, userId);
    }

    public void onStartClick() {
        App.changeSceneToMessagesWindow(service, userId);
    }

    public void onNowClick() {
        App.changeSceneToAddFriendsWindow(service, userId);
    }
}
