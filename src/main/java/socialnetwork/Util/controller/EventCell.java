package socialnetwork.Util.controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import socialnetwork.App;
import socialnetwork.domain.Event;

import static socialnetwork.Util.Constants.eventDateTime;
//440

public final class EventCell extends ListCell<Event> {
    private final ImageView eventImage = new ImageView();
    private double cellLength, imageWidth, imageHeight;
    private boolean wantDescription;

    public EventCell(double imageWidth, double imageHeight, double cellLength, boolean wantDescription) {
        this.imageWidth = imageWidth;
        this.imageHeight= imageHeight;
        this.cellLength = cellLength;
        this.wantDescription=wantDescription;
    }

    @Override
    protected void updateItem(Event event, boolean empty){
        super.updateItem(event, empty);
        if (empty) {
            setGraphic(null);
            setText(null);
            setStyle("-fx-background-color: #243142");
        } else {
            HBox hbox = new HBox();
            VBox vbox = new VBox();

            hbox.getChildren().clear();
            vbox.getChildren().clear();
            eventImage.setFitWidth(imageWidth);
            eventImage.setFitHeight(imageHeight);
            eventImage.setImage(new Image(event.getImagePath()));
            vbox.setPadding(new Insets(15));

            Text descriptionText = new Text(wantDescription ? event.getDescription() : "");

            descriptionText.getStyleClass().add("fancyText");
            TextFlow descriptionFlow = new TextFlow(descriptionText);
            Text date = new Text(event.getDate().format(eventDateTime));
            date.getStyleClass().add("fancyText");
            TextFlow dateFlow = new TextFlow(date);
            Text title = new Text(event.getTitle());
            title.getStyleClass().add("fancyTitle");
            TextFlow titleFlow = new TextFlow(title);
            vbox.getChildren().addAll(dateFlow, titleFlow, descriptionFlow);
            vbox.setAlignment(Pos.CENTER_LEFT);
            hbox.setPadding(new Insets(15));
            hbox.getChildren().addAll(eventImage, vbox);

            hbox.setMaxWidth(cellLength);
            hbox.setAlignment(Pos.CENTER_LEFT);
            setGraphic(hbox);
            if (isSelected()) {
                setStyle("-fx-background-color: #1c2a36");
            } else {
                setStyle("-fx-background-color: #243142");
            }
        }
    }
}
