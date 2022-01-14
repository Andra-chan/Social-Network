package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import socialnetwork.App;
import socialnetwork.Util.controller.FriendListCell;
import socialnetwork.Util.controller.NotificationService;
import socialnetwork.domain.Friend;
import socialnetwork.domain.Message;
import socialnetwork.service.Service;
import socialnetwork.service.paging.PageableImplementation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static socialnetwork.Util.Constants.eventDateTime;

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

    @FXML
    Label warningLabel;

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
        messagesList.setItems(modelMessages);
        messagesList.setCellFactory(param -> new ListCell<>() {
            @Override
            public void updateItem(Message message, boolean empty) {
                if (empty) {
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-background-color: #243142");
                } else {
                    VBox vbox = new VBox();
                    vbox.getChildren().clear();
                    vbox.setPadding(new Insets(15));
                    Text messageText = new Text(message.getMessageBody());
                    messageText.getStyleClass().add("fancyText");
                    TextFlow messageFlow = new TextFlow(messageText);
                    Text sender = new Text("from: " + message.getFrom().getFirstName() + " "
                            + message.getFrom().getLastName() + " @ " + message.getDate().format(eventDateTime));
                    sender.getStyleClass().add("fancyText");
                    TextFlow senderFlow = new TextFlow(sender);
                    vbox.getChildren().addAll(senderFlow, messageFlow);
                    setGraphic(vbox);
                }
            }
        });
        notificationService = new NotificationService(service, userId, notificationsButton,
                notificationButtonImage, String.valueOf(App.class.getResource("images/notificationsImage.png")),
                String.valueOf(App.class.getResource("images/activeNotifications.png")));
        notificationService.setPeriod(Duration.seconds(5));
        notificationService.start();
    }

    public List<Friend> getFriends() {
        return service.getFriendsFilteredPaged(userId, "", "",
                new PageableImplementation(currentFriendsPage, friendsPageSize));
    }

    private void updateFriendsList() {
        modelFriendships.setAll(getFriends());

        noFriendsLabel.setVisible(modelFriendships.size() == 0);
        noFriendsImage.setVisible(modelFriendships.size() == 0);
        friendsList.setVisible(modelFriendships.size() != 0);
    }

    public List<Message> getReceivedMessages() {
        return service.getReceivedMessagesFromTimePeriodPaged(userId,
                LocalDateTime.of(startDate.getValue(), LocalTime.of(0, 0)),
                LocalDateTime.of(endDate.getValue(), LocalTime.of(23, 59, 59)),
                new PageableImplementation(currentMessagesPage, messagesPageSize));
    }

    public void updateReceivedMessaged() {
        modelMessages.setAll(getReceivedMessages());
        noMessagesImage.setVisible(modelMessages.size() == 0);
        noMessagesLabel.setVisible(modelMessages.size() == 0);
        messagesList.setVisible(modelMessages.size() != 0);
    }

    public List<Friend> getFriendsFiltered() {
        return service.getNewFriendsFromTimePeriodPaged(userId,
                LocalDateTime.of(startDate.getValue(), LocalTime.of(0, 0)),
                LocalDateTime.of(endDate.getValue(), LocalTime.of(23, 59, 59)),
                new PageableImplementation(currentFriendsPage, friendsPageSize));
    }

    private void updateFriendsFiltered() {
        modelFriendships.setAll(getFriendsFiltered());
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
        LocalDate now = LocalDate.now();
        if (startDate == null || endDate == null
                || startDate.getValue().isAfter(now)
                || endDate.getValue().isAfter(now)
                || startDate.getValue().isAfter(endDate.getValue())){
            warningLabel.setVisible(true);
            warningLabel.setText("Invalid date!");
        }
        updateFriendsFiltered();
        updateReceivedMessaged();
        warningLabel.setVisible(false);
    }

    public void onPreviewR2ButtonClick() {
        isR2 = true;
        currentMessagesPage = 0;
        currentFriendsPage = 0;
        stateR2Visibility(false);
        updateFriendsList();
    }

    public void onFriendsPreviousPageButtonClick() {
        if (currentFriendsPage == 0) {
            return;
        }
        currentFriendsPage--;
        modelFriendships.setAll(getFriends());
    }

    public void onFriendsNextPageButtonClick() {
        currentFriendsPage++;
        var friends = getFriends();
        if (friends.isEmpty()) {
            currentFriendsPage--;
            return;
        }
        modelFriendships.setAll(friends);
    }

    public void onMessagesNextPageButtonClick() {

    }

    public void onMessagesPreviousPageButtonClick() {

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
