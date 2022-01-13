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
import socialnetwork.Util.events.ChangeObserverEvent;
import socialnetwork.Util.events.ChangeObserverEventType;
import socialnetwork.Util.observer.Observer;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static socialnetwork.Util.imageHelper.Helpers.setProfileImage;

public class FriendRequestsController implements Observer<ChangeObserverEvent> {
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
    Button homeButton;

    @FXML
    Button notificationsButton;

    @FXML
    Button eventsButton;

    @FXML
    Label noMutualFriendsLabel;

    @FXML
    ImageView noMutualFriendsImage;

    @FXML
    ListView mutualFriendsList;

    @FXML
    Label mutualFriendsLabel;

    @FXML
    Label mutualNowLabel;

    @FXML
    Button reportsButton;

    /**
     * Initialize UI elements.
     */
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
        noMutualFriendsLabel.setVisible(false);
        noMutualFriendsImage.setVisible(false);
        mutualFriendsList.setVisible(false);
        mutualFriendsLabel.setVisible(false);
        mutualNowLabel.setVisible(false);


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
                    //profileImage.setPreserveRatio(true);
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
                setProfileImage(newValue, userImage);
            }

        });
        userList.setItems(modelUsersWithFriendRequests);
    }

    /**
     * Initialize data using the service, and logged-in user
     * @param service the Service
     * @param userId the logged-in users' id
     */
    public void initData(Service service, Long userId) {
        this.service=service;
        this.userId=userId;
        updateModel();
        service.addObserver(this);
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
    }

    /**
     * Update the data model with new data from the service.
     */
    public void updateModel(){
        Predicate<Utilizator> firstNameFilter = u -> u.getFirstName().startsWith(searchField.getText());
        Predicate<Utilizator> lastNameFilter = u -> u.getLastName().startsWith(searchField.getText());
        modelUsersWithFriendRequests.setAll(getUsersListWithFriendRequests()
                .stream().filter(firstNameFilter.or(lastNameFilter)).collect(Collectors.toList()));
    }

    /**
     * Get the list of users directed to the logged in user
     * @return a list of users with friend requests directed to the logged in user
     */
    public List<Utilizator> getUsersListWithFriendRequests(){
        return service.getPendingFriendRequests(userId).stream().map(x -> service.getUser(x.getSender()))
                .collect(Collectors.toList());
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

    /**
     * When the user clicks on the accept button, accept the friend request from the selected user.
     */
    public void onAcceptButtonClick(){
        var selectedFriend = userList.getSelectionModel().getSelectedItem();
        if(selectedFriend!=null) {
            var friendRequest = service.getFriendRequest(userId, selectedFriend.getId());
            if(friendRequest.isPresent()){
                service.handleFriendRequest(friendRequest.get().getId(), "A");
            }
        }
    }

    /**
     * When the user clicks on the decline button, decline the friend request from the selected user.
     */
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

    public void onHomeButtonClick(){
        App.changeSceneToMainWindow(service, userId);
    }

    public void onReportsButtonClick(){
        App.changeSceneToReportsWindow(service, userId);
    }



    /**
     * Update data with new friend request data from the service.
     * @param event the event type.
     */
    @Override
    public void update(ChangeObserverEvent event) {
        if (event.getType().equals(ChangeObserverEventType.FRIEND_REQUEST)) {
            updateModel();
            return;
        }
    }
}
