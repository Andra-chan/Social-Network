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

    private void setVisibleUserInfo(){
        friendImage.setVisible(true);
        friendNameLabel.setVisible(true);
        separator.setVisible(true);
        noFriendSelectedImage.setVisible(false);
        noFriendSelectedLabel.setVisible(false);
    }

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
            } else{
                setVisibleUserInfo();
                friendNameLabel.setText(newValue.getFirstName() + " " + newValue.getLastName());
                var maybePending = service.getPendingFriendRequest(userId, newValue.getId());
                if(maybePending.isPresent()){
                    //you have a friend request from the selected user.
                    acceptRequestButton.setVisible(true);
                    denyRequestButton.setVisible(true);
                    cancelRequestButton.setVisible(false);
                    addButton.setVisible(false);
                    return;
                }
                var maybeOutgoing = service.getPendingFriendRequest(newValue.getId(), userId);
                if(maybeOutgoing.isPresent()){
                    //you have outgoing request to selected user.
                    cancelRequestButton.setVisible(true);
                    acceptRequestButton.setVisible(false);
                    denyRequestButton.setVisible(false);
                    addButton.setVisible(false);
                    return;
                }
                //you and the selected user are not friends.
                addButton.setVisible(true);
                cancelRequestButton.setVisible(false);
                denyRequestButton.setVisible(false);
                acceptRequestButton.setVisible(false);
            }
        });
        userList.setItems(modelUsers);
    }

    public void initData(Service service, Long userId) {
        this.service=service;
        this.userId=userId;
        service.addObserver(this);
        updateModel();
    }

    public void onMenuFriendsClick() {
        App.changeSceneToFriendsWindow(service, userId);
    }

    public void onMenuFriendRequestsClick() {
        App.changeSceneToFriendRequestsWindow(service, userId);
    }

    public void onMenuAddFriendsClick() {
        //App.changeSceneToAddFriendsWindow(service, userId);
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

    private List<Utilizator> getUserList(){
        return service.getAllUsersThatAreNotFriends(userId);
    }

    public void updateModel() {
        Predicate<Utilizator> firstNameFilter = u -> u.getFirstName().startsWith(searchField.getText());
        Predicate<Utilizator> lastNameFilter = u -> u.getLastName().startsWith(searchField.getText());
        modelUsers.setAll(getUserList()
                .stream().filter(firstNameFilter.or(lastNameFilter)).collect(Collectors.toList()));
    }

    public void onAddButtonPress() {
        var selectedUser = userList.getSelectionModel().getSelectedItem();
        if(selectedUser!=null){
            service.addFriendRequest(new FriendRequest(userId, selectedUser.getId()));
        }
        userList.getSelectionModel().select(selectedUser);

    }

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

    public void onAcceptRequestButtonClick() {
        var selectedUser = userList.getSelectionModel().getSelectedItem();
        if(selectedUser!=null) {
            var maybeIncoming = service.getPendingFriendRequest(userId, selectedUser.getId());
            if(maybeIncoming.isPresent()){
                service.handleFriendRequest(maybeIncoming.get().getId(), "A");
            }
        }
        userList.getSelectionModel().select(selectedUser);

    }

    public void onDenyRequestButtonClick() {
        var selectedUser = userList.getSelectionModel().getSelectedItem();
        if(selectedUser!=null) {
            var maybeIncoming = service.getPendingFriendRequest(userId, selectedUser.getId());
            if(maybeIncoming.isPresent()){
                service.handleFriendRequest(maybeIncoming.get().getId(), "R");
            }
        }
        userList.getSelectionModel().select(selectedUser);

    }

    @Override
    public void update(ChangeEvent event) {
        if (event.getType().equals(ChangeEventType.FRIEND_REQUEST) ||
                event.getType().equals(ChangeEventType.FRIENDSHIP)) {
            updateModel();
            return;
        }
    }
}

