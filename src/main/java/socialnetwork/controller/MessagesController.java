package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import socialnetwork.App;
import socialnetwork.Util.controller.FriendListCell;
import socialnetwork.Util.controller.NotificationService;
import socialnetwork.Util.message.MessageType;
import socialnetwork.domain.Friend;
import socialnetwork.domain.MessageDTO;
import socialnetwork.service.Service;
import socialnetwork.service.paging.PageableImplementation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static socialnetwork.Util.imageHelper.Helpers.setProfileImage;

public class MessagesController {
    Service service;
    Long userId;
    ObservableList<Friend> modelFriendships = FXCollections.observableArrayList();
    NotificationService notificationService;
    int currentFriendsPage = 0;
    final int friendsPageSize = 10;

    @FXML
    public ScrollPane chatScrollPane;

    @FXML
    public GridPane chatGridPane;

    @FXML
    public ListView<Friend> friendList;

    @FXML
    public TextField chatTextField;

    @FXML
    public TextField searchField;

    @FXML
    public Label userNameLabel;

    @FXML
    public ImageView userNameImage;

    @FXML
    public Separator separator1;

    @FXML
    public Separator separator2;

    @FXML
    public Separator separator3;

    @FXML
    public Label noMessagesLabel;

    @FXML
    public ImageView noMessagesImage;

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
    Button sendButton;

    @FXML
    ImageView noFriendSelectedImage;

    @FXML
    Label noFriendSelectedLabel;

    @FXML
    Button homeButton;

    @FXML
    Button notificationsButton;

    @FXML
    Button eventsButton;

    @FXML
    Button reportsButton;

    @FXML
    ImageView notificationButtonImage;

    @FXML
    Label nowLabel;

    @FXML
    Label noFriendsLabel;

    @FXML
    ImageView noFriendsImage;

    @FXML
    Button friendsNextPage;

    @FXML
    Button friendsPrevPage;

    /**
     * Set chat elements to a certain visibility state
     *
     * @param state the visibility state you want to set the chat elements to
     */
    public void setChatObjectsVisibility(boolean state) {
        chatTextField.setVisible(state);
        userNameImage.setVisible(state);
        userNameLabel.setVisible(state);
        chatGridPane.setVisible(state);
        sendButton.setVisible(state);
        chatScrollPane.setVisible(state);
        separator1.setVisible(state);
        separator2.setVisible(state);
    }

    private void setButtonsToDefaultState(boolean state) {
        friendList.setVisible(!state);
        noFriendsImage.setVisible(state);
        noFriendsLabel.setVisible(state);
        nowLabel.setVisible(state);
    }

    /**
     * Set usage notification to a certain visibility state
     *
     * @param state the visibility state
     */
    public void setNotifyUsageVisibility(boolean state) {
        noFriendSelectedImage.setVisible(state);
        noFriendSelectedLabel.setVisible(state);
    }

    /**
     * Set the 'no-messages notification' to a certain visibility state
     *
     * @param state the visibility state
     */
    public void setNotifyNoMessagesVisibility(boolean state) {
        noMessagesImage.setVisible(state);
        noMessagesLabel.setVisible(state);
    }

    /**
     * Update the message list and update UI with a new state.
     */
    private void updateMessagesAndUpdateUI() {
        var messages = getAllMessages();
        if (messages.isEmpty()) {
            setNotifyNoMessagesVisibility(true);
            setChatObjectsVisibility(true);
            setNotifyUsageVisibility(false);
            return;
        }
        updateMessages(messages);
    }

    /**
     * Initializes the UI elements.
     */
    @FXML
    public void initialize() {
        setNotifyUsageVisibility(true);
        setNotifyUsageVisibility(false);
        setNotifyNoMessagesVisibility(false);
        chatScrollPane.vvalueProperty().bind(chatGridPane.heightProperty());
        chatTextField.setOnKeyPressed(event -> enterKeyPressed(event.getCode()));
        friendList.setCellFactory(param -> new FriendListCell());

        friendList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                setChatObjectsVisibility(false);
                setNotifyUsageVisibility(true);
                setNotifyNoMessagesVisibility(false);
            } else {
                setNotifyUsageVisibility(false);
                setChatObjectsVisibility(true);
                setNotifyNoMessagesVisibility(false);
                setProfileImage(newValue, userNameImage);
                userNameLabel.setText(newValue.getFirstName() + " " + newValue.getLastName());
                chatGridPane.getChildren().clear();
                updateMessagesAndUpdateUI();
            }
        });
    }

    /**
     * Adds a message to the gridpane
     *
     * @param message a message
     */
    private void addMessageToChat(MessageDTO message) {
        if (message == null) {
            return;
        }
        final Text agent = message.getType().equals(MessageType.SEND) ? new Text("You") : new Text(message.getFrom());
        Text recordMessage = new Text(message.getMessageBody());
        final var messageFlow = new TextFlow(recordMessage);

        if (message.getType().equals(MessageType.SEND)) {
            recordMessage.getStyleClass().add("sentMessageContent");
            messageFlow.getStyleClass().addAll("sentMessage", "message");
        } else {
            recordMessage.getStyleClass().add("receivedMessageContent");
            messageFlow.getStyleClass().addAll("receivedMessage", "message");
        }
        agent.getStyleClass().add("messageAgent");
        messageFlow.setLineSpacing(2);

        final var columnIndex = message.getType().equals(MessageType.SEND) ? 1 : 0;
        final var columnSpan = 2;

        final var rowIndex = chatGridPane.getRowCount();
        final var rowSpan = 1;

        VBox vBox = getVBox(agent, messageFlow);
        chatGridPane.add(vBox, columnIndex, rowIndex, columnSpan, rowSpan);
    }


    /**
     * Return a VBox containing the given nodes
     *
     * @param nodes some nodes
     * @return a VBox containing all the nodes.
     */
    private VBox getVBox(Node... nodes) {
        final var vbox = new VBox(nodes);
        vbox.setPadding(new Insets(10));
        return vbox;
    }

    /**
     * Update messages from the grid using the provided message list
     * @param messages a message list
     */
    private void updateMessages(List<MessageDTO> messages) {

        for (var message : messages) {
            addMessageToChat(message);
        }
    }

    /**
     * Update the friendship model.
     */
    public void updateModel() {
        if (!(searchField.getText().isBlank())) {
            currentFriendsPage = 0;
        }
        modelFriendships.setAll(getFriends());
    }

    /**
     * Get all the messages between the logged-in user and the selected user from the list.
     * @return a  list of all messages between the logged-in user and the selected user.
     */
    private List<MessageDTO> getAllMessages() {
        var selectedUser = friendList.getSelectionModel().getSelectedItem();
        if (selectedUser == null)
            return new ArrayList<MessageDTO>();

        return service.getMessageListBetweenTwoUsers(userId, selectedUser.getId())
                .stream()
                .map(x -> new MessageDTO(
                        x.getId(),
                        x.getReply() != null ? x.getReply().getId() : null,
                        x.getMessageBody(),
                        x.getFrom().getId().equals(userId) ? MessageType.SEND : MessageType.RECEIVE,
                        x.getFrom().getFirstName() + " " + x.getFrom().getLastName()))
                .collect(Collectors.toList());
    }

    /**
     * Initializes data using the service and logged-in user.
     * @param service the Service
     * @param userId the logged-in users' id.
     */
    public void initData(Service service, Long userId) {
        this.service = service;
        this.userId = userId;
        updateModel();
        friendList.setItems(modelFriendships);
        setButtonsToDefaultState(modelFriendships.isEmpty());
        updateMessagesAndUpdateUI();
        setNotifyNoMessagesVisibility(false);
        setChatObjectsVisibility(false);
        setNotifyUsageVisibility(true);
        notificationService = new NotificationService(service, userId, notificationsButton,
                notificationButtonImage, String.valueOf(App.class.getResource("images/notificationsImage.png")),
                String.valueOf(App.class.getResource("images/activeNotifications.png")));
        notificationService.setPeriod(Duration.seconds(5));
        notificationService.start();
    }

    /**
     * Get the last message added.
     * @return the last message added.
     */
    private MessageDTO getLastMessage() {
        var messages = getAllMessages();
        if (messages.size() == 0) {
            return null;
        }
        return messages.get(messages.size() - 1);
    }

    public List<Friend> getFriends(){
        return service.getFriendsFilteredPaged(userId, searchField.getText(), searchField.getText(), new PageableImplementation(currentFriendsPage, friendsPageSize));
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
    public void onMenuAddFriendsClick() {
        App.changeSceneToAddFriendsWindow(service, userId);
    }

    public void onMenuMessagesClick() {
        //App.changeSceneToMessagesWindow(service, userId);
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

    public void onHomeButtonClick(){
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

    public void onNowClick(){
        App.changeSceneToAddFriendsWindow(service, userId);
    }

    /**
     * When you press the send button, send a message to the selected user.
     */
    public void onSendButtonPress() {
        var maybeReceiver = friendList.getSelectionModel().getSelectedItem();
        if (maybeReceiver == null)
            return;
        var receiver = maybeReceiver.getId();

        String messageText = chatTextField.getText();
        if (!messageText.isBlank()) {
            ArrayList<Long> to = new ArrayList<>();
            to.add(receiver);
            var message = service.sendMessage(userId, to, messageText);
            if (message == null) {
                addMessageToChat(getLastMessage());
                setNotifyNoMessagesVisibility(false);
            }
            chatTextField.clear();
        }
    }

    public void enterKeyPressed(KeyCode code) {
        if (code.equals(KeyCode.ENTER)) {
            onSendButtonPress();
        }
    }
}
