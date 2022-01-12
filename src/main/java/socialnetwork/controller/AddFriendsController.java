package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import socialnetwork.App;
import socialnetwork.Util.events.ChangeEvent;
import socialnetwork.Util.events.ChangeEventType;
import socialnetwork.Util.observer.Observable;
import socialnetwork.Util.observer.Observer;
import socialnetwork.domain.Friend;
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static socialnetwork.Util.imageHelper.Helpers.setProfileImage;

public class AddFriendsController implements Observer<ChangeEvent> {
    Service service;
    Long userId;
    ObservableList<Utilizator> modelUsers = FXCollections.observableArrayList();

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
    TextField searchField;

    @FXML
    ListView<Utilizator> userList;

    @FXML
    Label friendNameLabel;

    @FXML
    ImageView friendImage;

    @FXML
    Button addButton;

    @FXML
    Separator separator;

    @FXML
    ImageView addFriendImage;

    @FXML
    ImageView noFriendSelectedImage;

    @FXML
    Label noFriendSelectedLabel;

    @FXML
    Button cancelRequestButton;

    @FXML
    Button acceptRequestButton;

    @FXML
    Button denyRequestButton;

    @FXML
    ImageView noUsersImage;

    @FXML
    Label noUsersLabel;

    @FXML
    Button homeButton;


    /**
     * Sets visibility of all required objects to the default one
     */
    private void setButtonsToDefaultState(){
        friendImage.setVisible(false);
        friendNameLabel.setVisible(false);
        addButton.setVisible(false);
        separator.setVisible(false);
        addFriendImage.setVisible(false);
        noFriendSelectedImage.setVisible(true);
        noFriendSelectedLabel.setVisible(true);
        cancelRequestButton.setVisible(false);
        denyRequestButton.setVisible(false);
        acceptRequestButton.setVisible(false);

    }

    /**
     * Sets the visibility of user into
     */
    private void setVisibleUserInfo(){
        friendImage.setVisible(true);
        friendNameLabel.setVisible(true);
        separator.setVisible(true);
        noFriendSelectedImage.setVisible(false);
        noFriendSelectedLabel.setVisible(false);
    }

    /**
     * Initializes the controller
     */
    @FXML
    public void initialize(){
        setButtonsToDefaultState();
        userList.setCellFactory(param -> new ListCell<>(){
            private ImageView profileImage = new ImageView(String.valueOf(App.class.getResource("images/defaultUserImage.png")));

            @Override
            public void updateItem(Utilizator user, boolean empty) {
                super.updateItem(user, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-background-color: #243142");

                } else {
                    profileImage.setFitHeight(64);
                    profileImage.setFitWidth(64);
                    profileImage.setBlendMode(BlendMode.DARKEN);
                    profileImage.setPreserveRatio(true);
                    setProfileImage(user, profileImage);
                    setText(user.getFirstName() + " " + user.getLastName());
                    setGraphic(profileImage);
                    setTextFill(Color.WHITE);
                    if(isSelected())
                        setStyle("-fx-background-color: #1c2a36");
                    else{
                        setStyle("-fx-background-color: #243142");
                    }
                }
            }
        });

        userList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null) {
                setButtonsToDefaultState();
            } else {
                setVisibleUserInfo();
                friendNameLabel.setText(newValue.getFirstName() + " " + newValue.getLastName());
                setProfileImage(newValue, friendImage);
                var maybePending = service.getFriendRequest(userId, newValue.getId());
                if (maybePending.isPresent()) {
                    var pendingRequest = maybePending.get();
                    if (pendingRequest.getStatus().equals("REJECTED")) {
                        acceptRequestButton.setVisible(false);
                        denyRequestButton.setVisible(false);
                        cancelRequestButton.setVisible(false);
                        addButton.setVisible(false);
                        return;
                    }
                    if (pendingRequest.getSender().equals(userId)) {
                        cancelRequestButton.setVisible(true);
                        acceptRequestButton.setVisible(false);
                        denyRequestButton.setVisible(false);
                        addButton.setVisible(false);
                        return;
                    }
                    if (pendingRequest.getReceiver().equals(userId)) {
                        acceptRequestButton.setVisible(true);
                        denyRequestButton.setVisible(true);
                        cancelRequestButton.setVisible(false);
                        addButton.setVisible(false);
                        return;
                    }
                } else {
                    addButton.setVisible(true);
                    cancelRequestButton.setVisible(false);
                    denyRequestButton.setVisible(false);
                    acceptRequestButton.setVisible(false);
                }
            }
        });
        userList.setItems(modelUsers);
    }

    /**
     * Initialize the data inside the controller
     * @param service a Service
     * @param userId the logged in user's id
     */
    public void initData(Service service, Long userId) {
        this.service=service;
        this.userId=userId;
        service.addObserver(this);
        updateModel();
        if(modelUsers.isEmpty()){
            noUsersImage.setVisible(true);
            noUsersLabel.setVisible(true);
        }
        else{
            noUsersImage.setVisible(false);
            noUsersLabel.setVisible(false);
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

    public void onMenuAddFriendsClick() {
        //App.changeSceneToAddFriendsWindow(service, userId);
    }

    /**
     * When the user clicks on the mesasges button, switch the scene
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
     * When the user clicks on the logout button, switch the scene to the login scene
     */
    public void onLogoutButtonClick(){
        App.changeSceneToLogin(service);
    }

    /**
     * @return a list of users that are not friends with the logged-in user
     */
    private List<Utilizator> getUserList(){
        return service.getAllUsersThatAreNotFriends(userId);
    }

    /**
     * Updates the modelUsers with new data from service.
     */
    public void updateModel() {
        Predicate<Utilizator> firstNameFilter = u -> u.getFirstName().startsWith(searchField.getText());
        Predicate<Utilizator> lastNameFilter = u -> u.getLastName().startsWith(searchField.getText());
        modelUsers.setAll(getUserList()
                .stream().filter(firstNameFilter.or(lastNameFilter)).collect(Collectors.toList()));
    }

    /**
     * When the user presses the add button, send a friend request to the selected user
     */
    public void onAddButtonPress() {
        var selectedUser = userList.getSelectionModel().getSelectedItem();
        if(selectedUser!=null){
            service.addFriendRequest(new FriendRequest(userId, selectedUser.getId()));
        }
        userList.getSelectionModel().select(selectedUser);

    }

    /**
     * When the user presses the cancel button, cancel the friend request to the selected user
     */
    public void onCancelRequestButtonClick() {
        var selectedUser = userList.getSelectionModel().getSelectedItem();
        if(selectedUser!=null){
            var maybeOutgoing = service.getPendingFriendRequest(selectedUser.getId(), userId);
            if(maybeOutgoing.isPresent()) {
                service.stopFriendRequest(maybeOutgoing.get().getId());
            }
        }
        userList.getSelectionModel().select(selectedUser);
    }

    /**
     * When the user clicks the accept button, accept the friend request sent by the selected user.
     */
    public void onAcceptRequestButtonClick() {
        var selectedUser = userList.getSelectionModel().getSelectedItem();
        if(selectedUser!=null) {
            var maybeIncoming = service.getPendingFriendRequest(userId, selectedUser.getId());
            if(maybeIncoming.isPresent()){
                service.handleFriendRequest(maybeIncoming.get().getId(), "A");
                setButtonsToDefaultState();
            }
        }

    }

    /**
     * When the user clicks on the deny button, deny the friend request sent by the selected user
     */
    public void onDenyRequestButtonClick() {
        var selectedUser = userList.getSelectionModel().getSelectedItem();
        if(selectedUser!=null) {
            var maybeIncoming = service.getPendingFriendRequest(userId, selectedUser.getId());
            if(maybeIncoming.isPresent()){
                service.handleFriendRequest(maybeIncoming.get().getId(), "R");
                setButtonsToDefaultState();
            }
        }

    }

    public void onHomeButtonClick(){
        App.changeSceneToMainWindow(service, userId);
    }

    /**
     * Updates friend data with new data from the service.
     * @param event the event type
     */
    @Override
    public void update(ChangeEvent event) {
        if (event.getType().equals(ChangeEventType.FRIEND_REQUEST) ||
                event.getType().equals(ChangeEventType.FRIENDSHIP)) {
            updateModel();
            return;
        }
    }
}

