package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import socialnetwork.App;
import socialnetwork.Util.controller.FriendListCell;
import socialnetwork.Util.controller.NotificationService;
import socialnetwork.Util.controller.UserListCell;
import socialnetwork.Util.events.ChangeObserverEvent;
import socialnetwork.Util.events.ChangeObserverEventType;
import socialnetwork.Util.observer.Observer;
import socialnetwork.domain.Friend;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.Service;
import socialnetwork.service.paging.Page;
import socialnetwork.service.paging.PageableImplementation;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static socialnetwork.Util.imageHelper.Helpers.setProfileImage;

public class FriendRequestsController implements Observer<ChangeObserverEvent> {
    Service service;
    Long userId;
    ObservableList<Utilizator> modelUsersWithFriendRequests = FXCollections.observableArrayList();
    ObservableList<Friend> modelCommonFriends = FXCollections.observableArrayList();
    NotificationService notificationService;
    int currentPage = 0;
    final int pageSize = 10;

    int currentFriendsPage = 0;
    final int friendsPageSize = 5;

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
    ListView<Friend> mutualFriendsList;

    @FXML
    Label mutualFriendsLabel;

    @FXML
    Label mutualNowLabel;

    @FXML
    Button reportsButton;

    @FXML
    Button nextPage;

    @FXML
    Button prevPage;

    @FXML
    Button friendsPrevPage;

    @FXML
    Button friendsNextPage;

    /**
     * Initialize UI elements.
     */
    @FXML
    public void initialize() {
        setNoFriendSelectedState(false);
        mutualFriendsList.setItems(modelCommonFriends);
        mutualFriendsList.setCellFactory(x -> new FriendListCell());

        userList.setCellFactory(param -> new UserListCell());
        userList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                setNoFriendSelectedState(false);
            } else {
                userNameLabel.setText(newValue.getFirstName() + " " + newValue.getLastName());
                setNoFriendSelectedState(true);
                setProfileImage(newValue, userImage);
                currentFriendsPage = 0;
                modelCommonFriends.setAll(service.getCommonFriends(userId, newValue.getId()));
                modelCommonFriends.setAll(service.getCommonFriendsPaged(userId, newValue.getId(), new PageableImplementation(currentFriendsPage, friendsPageSize)));
                noMutualFriendsImage.setVisible(modelCommonFriends.size() == 0);
                noMutualFriendsLabel.setVisible(modelCommonFriends.size() == 0);
                mutualNowLabel.setVisible(modelCommonFriends.size() == 0);
                mutualFriendsList.setVisible(!(modelCommonFriends.size() == 0));
                mutualFriendsLabel.setVisible(!(modelCommonFriends.size() == 0));
            }
        });
        userList.setItems(modelUsersWithFriendRequests);
    }

    /**
     * Initialize data using the service, and logged-in user
     *
     * @param service the Service
     * @param userId  the logged-in users' id
     */
    public void initData(Service service, Long userId) {
        this.service = service;
        this.userId = userId;
        updateModel();
        service.addObserver(this);
        if (modelUsersWithFriendRequests.isEmpty()) {
            noFriendRequestsImage.setVisible(true);
            noFriendRequestsLabel.setVisible(true);
            nowLabel.setVisible(true);
        } else {
            noFriendRequestsImage.setVisible(false);
            noFriendRequestsLabel.setVisible(false);
            nowLabel.setVisible(false);
        }
        notificationService = new NotificationService(service, userId, notificationsButton,
                notificationButtonImage, String.valueOf(App.class.getResource("images/notificationsImage.png")),
                String.valueOf(App.class.getResource("images/activeNotifications.png")));
        notificationService.setPeriod(Duration.seconds(5));
        notificationService.start();
    }

    private List<Utilizator> getUsers(){
        return service.getUsersListWithFriendRequestsFilteredAndPaged(userId, searchField.getText(), searchField.getText(), new PageableImplementation(currentPage, pageSize));
    }

    /**
     * Update the data model with new data from the service.
     */
    public void updateModel() {
        if (!(searchField.getText().isBlank())) {
            currentPage = 0;
        }
        modelUsersWithFriendRequests.setAll(getUsers());
    }

    public void onFriendsPreviousPageButtonClick() {
        var selectedFriend = userList.getSelectionModel().getSelectedItem();
        if (selectedFriend == null) {
            return;
        }
        if (currentFriendsPage == 0) {
            return;
        }
        currentFriendsPage--;
        modelCommonFriends.setAll(service.getCommonFriendsPaged(userId, selectedFriend.getId(), new PageableImplementation(currentFriendsPage, friendsPageSize)));
    }

    public void onFriendsNextPageButtonClick() {
        var selectedItem = userList.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        var friends = service.getCommonFriendsPaged(userId, selectedItem.getId(), new PageableImplementation(currentFriendsPage + 1, friendsPageSize));
        if (friends.isEmpty()) {
            return;
        }
        currentFriendsPage++;
        modelCommonFriends.setAll(friends);
    }

    public void onNextPageButtonClick() {
        currentPage++;
        var users = getUsers();
        if (users.isEmpty()) {
            currentPage--;
            return;
        }
        modelUsersWithFriendRequests.setAll(users);
    }

    public void onPreviousPageButtonClick() {
        if (currentPage == 0) {
            return;
        }
        currentPage--;
        updateModel();
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

    /**
     * When the user clicks on the messages button, switch the scene
     */
    public void onMenuMessagesClick() {
        App.changeSceneToMessagesWindow(service, userId);
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

    public void onEventsButtonClick() {
        App.changeSceneToEventsWindow(service, userId);
    }

    public void onNotificationsButtonClick() {
        App.changeSceneToMainWindow(service, userId);
    }

    /**
     * When the user clicks on the accept button, accept the friend request from the selected user.
     */
    public void onAcceptButtonClick() {
        var selectedFriend = userList.getSelectionModel().getSelectedItem();
        if (selectedFriend != null) {
            var friendRequest = service.getFriendRequest(userId, selectedFriend.getId());
            if (friendRequest.isPresent()) {
                service.handleFriendRequest(friendRequest.get().getId(), "A");
            }
        }
    }

    /**
     * When the user clicks on the decline button, decline the friend request from the selected user.
     */
    public void onDeclineButtonClick() {
        var selectedFriend = userList.getSelectionModel().getSelectedItem();
        if (selectedFriend != null) {
            var friendRequest = service.getFriendRequest(userId, selectedFriend.getId());
            if (friendRequest.isPresent()) {
                service.handleFriendRequest(friendRequest.get().getId(), "R");
            }
        }
    }

    public void onNowClick() {
        App.changeSceneToAddFriendsWindow(service, userId);
    }

    public void onHomeButtonClick() {
        App.changeSceneToMainWindow(service, userId);
    }

    public void onReportsButtonClick() {
        App.changeSceneToReportsWindow(service, userId);
    }


    @FXML
    ImageView notificationButtonImage;

    private void setNoFriendSelectedState(boolean state) {
        noUserSelectedImage.setVisible(!state);
        noUserSelectedLabel.setVisible(!state);
        userImage.setVisible(state);
        userNameLabel.setVisible(state);
        declineButton.setVisible(state);
        acceptButton.setVisible(state);
        separator.setVisible(state);
        declineFriendshipImage.setVisible(state);
        acceptFriendshipImage.setVisible(state);
        noMutualFriendsLabel.setVisible(state);
        noMutualFriendsImage.setVisible(state);
        mutualFriendsList.setVisible(state);
        mutualFriendsLabel.setVisible(state);
        mutualNowLabel.setVisible(state);
    }

    /**
     * Update data with new friend request data from the service.
     *
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
