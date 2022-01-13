package socialnetwork.Util.controller;

import javafx.scene.control.ListCell;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import socialnetwork.App;
import socialnetwork.domain.Friend;

import static socialnetwork.Util.imageHelper.Helpers.setProfileImage;

public class FriendListCell extends ListCell<Friend> {
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
}
