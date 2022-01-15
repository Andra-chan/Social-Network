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
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.Service;
import socialnetwork.service.paging.PageableImplementation;

import java.util.List;

import static socialnetwork.Util.imageHelper.Helpers.setProfileImage;

public class AddFriendsController implements Observer<ChangeObserverEvent> {
    Service service;
    Long userId;
    ObservableList<Utilizator> modelUsers = FXCollections.observableArrayList();
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
    Button reportsButton;

    @FXML
    ImageView notificationButtonImage;

    @FXML
    Button nextPage;

    @FXML
    Button prevPage;

    @FXML
    Button friendsPrevPage;

    @FXML
    Button friendsNextPage;

    /**
     * Sets visibility of all required objects to the default one
     */
    private void setButtonsToDefaultState() {
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
        noMutualFriendsLabel.setVisible(false);
        noMutualFriendsImage.setVisible(false);
        mutualFriendsList.setVisible(false);
        mutualFriendsLabel.setVisible(false);
        friendsPrevPage.setVisible(false);
        friendsNextPage.setVisible(false);
    }

    /**
     * Sets the visibility of user into
     */
    private void setVisibleUserInfo() {
        friendImage.setVisible(true);
        friendNameLabel.setVisible(true);
        separator.setVisible(true);
        noFriendSelectedImage.setVisible(false);
        noFriendSelectedLabel.setVisible(false);
        mutualFriendsList.setVisible(true);
    }

    /**
     * Initializes the controller
     */
    @FXML
    public void initialize() {
        setButtonsToDefaultState();

        mutualFriendsList.setItems(modelCommonFriends);
        mutualFriendsList.setCellFactory(param -> new FriendListCell());

        userList.setCellFactory(param -> new UserListCell());

        userList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                setButtonsToDefaultState();
            } else {
                currentFriendsPage = 0;
                setVisibleUserInfo();
                modelCommonFriends.setAll(service.getCommonFriendsPaged(userId, newValue.getId(), new PageableImplementation(currentFriendsPage, friendsPageSize)));

                mutualFriendsList.setVisible(!(modelCommonFriends.size() == 0));
                mutualFriendsLabel.setVisible(!(modelCommonFriends.size() == 0));
                friendsPrevPage.setVisible(!(modelCommonFriends.size() == 0));
                friendsNextPage.setVisible(!(modelCommonFriends.size() == 0));
                noMutualFriendsImage.setVisible(modelCommonFriends.size() == 0);
                noMutualFriendsLabel.setVisible(modelCommonFriends.size() == 0);
                friendsNextPage.setVisible(modelCommonFriends.size() != 0);
                friendsPrevPage.setVisible(modelCommonFriends.size() != 0);
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
     *
     * @param service a Service
     * @param userId  the logged in user's id
     */
    public void initData(Service service, Long userId) {
        this.service = service;
        this.userId = userId;
        service.addObserver(this);
        updateModel();
        if (modelUsers.isEmpty()) {
            noUsersImage.setVisible(true);
            noUsersLabel.setVisible(true);
            nextPage.setVisible(false);
            prevPage.setVisible(false);
        } else {
            noUsersImage.setVisible(false);
            noUsersLabel.setVisible(false);
            nextPage.setVisible(true);
            prevPage.setVisible(true);
        }
        notificationService = new NotificationService(service, userId, notificationsButton,
                notificationButtonImage, String.valueOf(App.class.getResource("images/notificationsImage.png")),
                String.valueOf(App.class.getResource("images/activeNotifications.png")));
        notificationService.setPeriod(Duration.seconds(5));
        notificationService.start();
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
        var users = getUserList();
        if (users.isEmpty()) {
            currentPage--;
            return;
        }
        modelUsers.setAll(users);
    }

    public void onPreviousPageButtonClick() {
        if (currentPage == 0) {
            return;
        }
        currentPage--;
        modelUsers.setAll(getUserList());
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
    public void onMenuSettingsClick() {
        App.changeSceneToSettingsWindow(service, userId);
    }

    /**
     * When the user clicks on the logout button, switch the scene to the login scene
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

    public void onHomeButtonClick() {
        App.changeSceneToMainWindow(service, userId);
    }

    public void onReportsButtonClick() {
        App.changeSceneToReportsWindow(service, userId);
    }

    /**
     * @return a list of users that are not friends with the logged-in user
     */
    private List<Utilizator> getUserList() {
        return service.getAllUsersThatAreNotFriendsFilteredAndPaged(userId, searchField.getText(), searchField.getText(), new PageableImplementation(currentPage, pageSize));
    }

    /**
     * Updates the modelUsers with new data from service.
     */
    public void updateModel() {
        if (!(searchField.getText().isBlank())) {
            currentPage = 0;
        }
        modelUsers.setAll(getUserList());
    }

    /**
     * When the user presses the add button, send a friend request to the selected user
     */
    public void onAddButtonPress() {
        var selectedUser = userList.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            service.addFriendRequest(new FriendRequest(userId, selectedUser.getId()));
        }
        userList.getSelectionModel().select(selectedUser);

    }

    /**
     * When the user presses the cancel button, cancel the friend request to the selected user
     */
    public void onCancelRequestButtonClick() {
        var selectedUser = userList.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            var maybeOutgoing = service.getPendingFriendRequest(selectedUser.getId(), userId);
            if (maybeOutgoing.isPresent()) {
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
        if (selectedUser != null) {
            var maybeIncoming = service.getPendingFriendRequest(userId, selectedUser.getId());
            if (maybeIncoming.isPresent()) {
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
        if (selectedUser != null) {
            var maybeIncoming = service.getPendingFriendRequest(userId, selectedUser.getId());
            if (maybeIncoming.isPresent()) {
                service.handleFriendRequest(maybeIncoming.get().getId(), "R");
                setButtonsToDefaultState();
            }
        }

    }

    /**
     * Updates friend data with new data from the service.
     *
     * @param event the event type
     */
    @Override
    public void update(ChangeObserverEvent event) {
        if (event.getType().equals(ChangeObserverEventType.FRIEND_REQUEST) ||
                event.getType().equals(ChangeObserverEventType.FRIENDSHIP)) {
            updateModel();
            return;
        }
    }
}

