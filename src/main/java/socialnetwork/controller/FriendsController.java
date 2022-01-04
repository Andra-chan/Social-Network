package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import socialnetwork.App;
import socialnetwork.domain.Friend;
import socialnetwork.service.Service;

import java.net.URL;

public class FriendsController {
    Service service;
    Long userId;
    ObservableList<Friend> modelFriendships = FXCollections.observableArrayList();

    @FXML
    AnchorPane friendsPane;

    @FXML
    AnchorPane friendRequestsPane;

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
    public void initialize(){
        friendList.setCellFactory(param ->  new ListCell<Friend>(){
            private ImageView profileImage =  new ImageView(String.valueOf(App.class.getResource("images/defaultUserImage.png")));
            @Override
            public void updateItem(Friend friend, boolean empty){
                super.updateItem(friend, empty);
                if(empty){
                    setGraphic(null);
                    setText(null);
                } else {
                    profileImage.setFitHeight(64);
                    profileImage.setFitWidth(64);
                    profileImage.setBlendMode(BlendMode.DARKEN);
                    setText(friend.getFirstName() + " " + friend.getLastName());
                    setGraphic(profileImage);
                }
            }
        });
        friendList.setItems(modelFriendships);
    }

    private void updateModel(){
        modelFriendships.setAll(service.getFriends(userId));
    }

    public void initData(Service service, Long userId) {
        this.service=service;
        this.userId=userId;
        updateModel();
    }

    public void onRemoveButtonPress(){
        var selectedFriend = friendList.getSelectionModel().getSelectedItem();
        if(selectedFriend!=null) {
            var friendship = service.getFriendship(userId, selectedFriend.getId());
            if(friendship.isPresent()){
                service.removeFriendship(friendship.get().getId());
            }
        }
    }

    public void onMenuFriendsClick() {
        App.changeSceneToFriendsWindow(service, userId);
    }

    public void onMenuFriendRequestsClick() {
        App.changeSceneToFriendRequestsWindow(service, userId);
    }

    public void onMenuMessagesClick() {
        App.changeSceneToMessagesWindow(service, userId);
    }

    public void onMenuSettingsClick(){
        App.changeSceneToSettingsWindow(service, userId);
    }
}
