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
import socialnetwork.Util.observer.Observer;
import socialnetwork.domain.Friend;
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FriendRequestsController implements Observer<ChangeEvent> {
    Service service;
    Long userId;
    ObservableList<Utilizator> modelUsersWithFriendRequests = FXCollections.observableArrayList();

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
    Button acceptButton;

    @FXML
    ImageView acceptFriendshipImage;

    @FXML
    Button declineButton;

    @FXML
    ImageView declineFriendshipImage;

    @FXML
    ImageView noUserSelectedImage;

    @FXML
    Label noUserSelectedLabel;

    @FXML
    TextField searchField;

    @FXML
    ListView<Utilizator> userList;

    @FXML
    Separator separator;

    @FXML
    Label userNameLabel;

    @FXML
    ImageView userImage;

    @FXML
    ImageView noFriendRequestsImage;

    @FXML
    Label noFriendRequestsLabel;

    @FXML
    Label nowLabel;


    @FXML
    public void initialize(){

        noUserSelectedImage.setVisible(true);
        noUserSelectedLabel.setVisible(true);
        userImage.setVisible(false);
        userNameLabel.setVisible(false);
        declineButton.setVisible(false);
        acceptButton.setVisible(false);
        separator.setVisible(false);
        declineFriendshipImage.setVisible(false);
        acceptFriendshipImage.setVisible(false);
        if(modelUsersWithFriendRequests.isEmpty()){
            noFriendRequestsImage.setVisible(true);
            noFriendRequestsLabel.setVisible(true);
            nowLabel.setVisible(true);
        }
        else{
            noFriendRequestsImage.setVisible(false);
            noFriendRequestsLabel.setVisible(false);
            nowLabel.setVisible(false);
        }

        userList.setCellFactory(param ->  new ListCell<Utilizator>(){
            private ImageView profileImage =  new ImageView(String.valueOf(App.class.getResource("images/defaultUserImage.png")));
            @Override
            public void updateItem(Utilizator user, boolean empty){
                super.updateItem(user, empty);
                if(empty){
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
            if(newValue == null){
                userImage.setVisible(false);
                userNameLabel.setVisible(false);
                declineButton.setVisible(false);
                acceptButton.setVisible(false);
                separator.setVisible(false);
                declineFriendshipImage.setVisible(false);
                acceptFriendshipImage.setVisible(false);
                noUserSelectedImage.setVisible(true);
                noUserSelectedLabel.setVisible(true);
            }
            else{
                userNameLabel.setText(newValue.getFirstName() + " " + newValue.getLastName());
                userImage.setVisible(true);
                userNameLabel.setVisible(true);
                declineButton.setVisible(true);
                acceptButton.setVisible(true);
                separator.setVisible(true);
                declineFriendshipImage.setVisible(true);
                acceptFriendshipImage.setVisible(true);
                noUserSelectedImage.setVisible(false);
                noUserSelectedLabel.setVisible(false);
            }

        });
        userList.setItems(modelUsersWithFriendRequests);
    }

    public void initData(Service service, Long userId) {
        this.service=service;
        this.userId=userId;
        updateModel();
        service.addObserver(this);
    }

    public void updateModel(){
        Predicate<Utilizator> firstNameFilter = u -> u.getFirstName().startsWith(searchField.getText());
        Predicate<Utilizator> lastNameFilter = u -> u.getLastName().startsWith(searchField.getText());
        modelUsersWithFriendRequests.setAll(getUsersListWithFriendRequests()
                .stream().filter(firstNameFilter.or(lastNameFilter)).collect(Collectors.toList()));
    }

    public List<Utilizator> getUsersListWithFriendRequests(){
        return service.getPendingFriendRequests(userId).stream().map(x -> service.getUser(x.getSender()))
                .collect(Collectors.toList());
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

    public void onAcceptButtonClick(){
        var selectedFriend = userList.getSelectionModel().getSelectedItem();
        if(selectedFriend!=null) {
            var friendRequest = service.getFriendRequest(userId, selectedFriend.getId());
            if(friendRequest.isPresent()){
                service.handleFriendRequest(friendRequest.get().getId(), "A");
            }
        }
    }

    public void onDeclineButtonClick(){
        var selectedFriend = userList.getSelectionModel().getSelectedItem();
        if(selectedFriend!=null) {
            var friendRequest = service.getFriendRequest(userId, selectedFriend.getId());
            if(friendRequest.isPresent()){
                service.handleFriendRequest(friendRequest.get().getId(), "R");
            }
        }
    }

    public void onNowClick(){
        App.changeSceneToAddFriendsWindow(service, userId);
    }

    @Override
    public void update(ChangeEvent event) {
        if (event.getType().equals(ChangeEventType.FRIEND_REQUEST)) {
            updateModel();
            return;
        }
    }
}
