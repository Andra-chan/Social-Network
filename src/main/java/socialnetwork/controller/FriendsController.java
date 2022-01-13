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
import socialnetwork.Util.controller.EventCell;
import socialnetwork.Util.events.ChangeObserverEvent;
import socialnetwork.Util.events.ChangeObserverEventType;
import socialnetwork.Util.observer.Observer;
import socialnetwork.domain.Event;
import socialnetwork.domain.Friend;
import socialnetwork.service.Service;

import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static socialnetwork.Util.imageHelper.Helpers.setProfileImage;

public class FriendsController implements Observer<ChangeObserverEvent> {
    Service service;
    Long userId;
    ObservableList<Friend> modelFriendships = FXCollections.observableArrayList();
    ObservableList<Event> modelEvents = FXCollections.observableArrayList();

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
    ListView<Friend> friendList;

    @FXML
    Label friendNameLabel;

    @FXML
    ImageView friendImage;

    @FXML
    Button removeButton;

    @FXML
    Separator separator;

    @FXML
    ImageView removeFriendImage;

    @FXML
    ImageView noFriendSelectedImage;

    @FXML
    Label noFriendSelectedLabel;

    @FXML
    TextField searchField;

    @FXML
    Button logoutButton;

    @FXML
    ImageView noFriendsImage;

    @FXML
    Label noFriendsLabel;

    @FXML
    Label nowLabel;

    @FXML
    Button homeButton;

    @FXML
    Button notificationsButton;

    @FXML
    Button eventsButton;

    @FXML
    Label mutualEventsLabel;

    @FXML
    ListView<Event> mutualEventsList;

    @FXML
    Label noMutualEventsLabel;

    @FXML
    Label mutualNowLabel;

    @FXML
    ImageView noMutualEventsImage;

    @FXML
    Button reportsButton;

    @FXML
    public void initialize() {

        noFriendSelectedImage.setVisible(true);
        noFriendSelectedLabel.setVisible(true);
        friendImage.setVisible(false);
        friendNameLabel.setVisible(false);
        removeButton.setVisible(false);
        separator.setVisible(false);
        removeFriendImage.setVisible(false);
        noMutualEventsLabel.setVisible(false);
        noMutualEventsImage.setVisible(false);
        mutualEventsList.setVisible(false);
        mutualEventsLabel.setVisible(false);
        mutualNowLabel.setVisible(false);

        mutualEventsList.setItems(modelEvents);
        mutualEventsList.setCellFactory(x -> new EventCell(160, 90, 400, false));

        friendList.setCellFactory(param -> new ListCell<>() {
            private ImageView profileImage = new ImageView(String.valueOf(App.class.getResource("images/defaultUserImage.png")));

            @Override
            public void updateItem(Friend friend, boolean empty) {
                super.updateItem(friend, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-background-color: #243142");

                } else {
                    profileImage.setFitHeight(64);
                    profileImage.setFitWidth(64);
                    profileImage.setBlendMode(BlendMode.DARKEN);
                    //profileImage.setPreserveRatio(true);
                    setProfileImage(friend, profileImage);
                    setText(friend.getFirstName() + " " + friend.getLastName());
                    setGraphic(profileImage);
                    setTextFill(Color.WHITE);
                    if (isSelected())
                        setStyle("-fx-background-color: #1c2a36");
                    else
                        setStyle("-fx-background-color: #243142");
                }
            }
        });
        friendList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                friendImage.setVisible(false);
                friendNameLabel.setVisible(false);
                removeButton.setVisible(false);
                separator.setVisible(false);
                removeFriendImage.setVisible(false);
                noFriendSelectedImage.setVisible(true);
                noFriendSelectedLabel.setVisible(true);
                mutualEventsList.setVisible(false);
                noMutualEventsLabel.setVisible(false);
                noMutualEventsImage.setVisible(false);
                mutualEventsLabel.setVisible(false);
            } else {
                friendNameLabel.setText(newValue.getFirstName() + " " + newValue.getLastName());
                friendImage.setVisible(true);
                friendNameLabel.setVisible(true);
                removeButton.setVisible(true);
                separator.setVisible(true);
                removeFriendImage.setVisible(true);
                noFriendSelectedImage.setVisible(false);
                noFriendSelectedLabel.setVisible(false);
                friendNameLabel.setText(newValue.getFirstName() + " " + newValue.getLastName());
                setProfileImage(newValue, friendImage);
                modelEvents.setAll(service.getCommonEvents(userId, newValue.getId()));
                noMutualEventsImage.setVisible(modelEvents.size() == 0);
                noMutualEventsLabel.setVisible(modelEvents.size() == 0);
                mutualEventsList.setVisible(modelEvents.size()!=0);
                mutualEventsLabel.setVisible(modelEvents.size()!=0);
            }
        });
        friendList.setItems(modelFriendships);
    }

    public void updateModel() {
        Predicate<Friend> firstNameFilter = u -> u.getFirstName().startsWith(searchField.getText());
        Predicate<Friend> lastNameFilter = u -> u.getLastName().startsWith(searchField.getText());
        modelFriendships.setAll(service.getFriends(userId)
                .stream().filter(firstNameFilter.or(lastNameFilter))
                .sorted(Comparator.comparing(Friend::getFirstName))
                .collect(Collectors.toList()));
    }

    public void initData(Service service, Long userId) {
        this.service = service;
        this.userId = userId;
        updateModel();
        service.addObserver(this);
        if (modelFriendships.isEmpty()) {
            noFriendsImage.setVisible(true);
            noFriendsLabel.setVisible(true);
            nowLabel.setVisible(true);
        } else {
            noFriendsImage.setVisible(false);
            noFriendsLabel.setVisible(false);
            nowLabel.setVisible(false);
        }
    }

    public void onRemoveButtonPress() {
        var selectedFriend = friendList.getSelectionModel().getSelectedItem();
        if (selectedFriend != null) {
            var friendship = service.getFriendship(userId, selectedFriend.getId());
            if (friendship.isPresent()) {
                service.removeFriendship(friendship.get().getId());
            }
        }
    }

    public void onNowClick() {
        App.changeSceneToAddFriendsWindow(service, userId);
    }

    public void onMenuFriendsClick() {
        App.changeSceneToFriendsWindow(service, userId);
    }

    public void onMenuFriendRequestsClick() {
        App.changeSceneToFriendRequestsWindow(service, userId);
    }

    public void onMenuAddFriendsClick() {
        App.changeSceneToAddFriendsWindow(service, userId);
    }

    public void onMenuMessagesClick() {
        App.changeSceneToMessagesWindow(service, userId);
    }

    public void onMenuSettingsClick() {
        App.changeSceneToSettingsWindow(service, userId);
    }

    public void onLogoutButtonClick() {
        App.changeSceneToLogin(service);
    }

    public void onHomeButtonClick() {
        App.changeSceneToMainWindow(service, userId);
    }

    public void onEventsButtonClick() {
        App.changeSceneToEventsWindow(service, userId);
    }

    public void onNotificationsButtonClick() {
        App.changeSceneToMainWindow(service, userId);
    }

    public void onMutualNowClick() {
        App.changeSceneToEventsWindow(service, userId);
    }

    public void onReportsButtonClick(){
        App.changeSceneToReportsWindow(service, userId);
    }

    @Override
    public void update(ChangeObserverEvent event) {
        if (event.getType().equals(ChangeObserverEventType.FRIENDSHIP)) {
            updateModel();
            return;
        }
    }
}
