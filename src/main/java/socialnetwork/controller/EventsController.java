package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import socialnetwork.App;
import socialnetwork.Util.events.ChangeObserverEvent;
import socialnetwork.Util.observer.Observer;
import socialnetwork.domain.Event;
import socialnetwork.service.Service;

import java.util.HashSet;
import java.util.Set;

import static socialnetwork.Util.Constants.eventDateTime;

public class EventsController implements Observer<ChangeObserverEvent> {
    Service service;
    Long userId;
    ObservableList<Event> modelEvents = FXCollections.observableArrayList();
    Set<Long> userAttendance;
    Set<Long> userEventsWithNotifications;

    @FXML
    Button notificationsButton;

    @FXML
    Button homeButton;

    @FXML
    Button logoutButton;

    @FXML
    Button newEventButton;

    @FXML
    ListView<Event> eventsList;

    @FXML
    Label nowLabel;

    @FXML
    ImageView noEventsImage;

    @FXML
    Label noEventsLabel;

    @FXML
    Button joinEventButton;

    @FXML
    Button leaveEventButton;

    @FXML
    ImageView noNotificationsImage;

    public EventsController() {
        userAttendance = new HashSet<>();
        userEventsWithNotifications = new HashSet<>();
    }

    private void setNoEventsState(boolean state){
        eventsList.setVisible(!state);
        noEventsImage.setVisible(state);
        noEventsLabel.setVisible(state);
        nowLabel.setVisible(state);
    }

    @FXML
    public void initialize() {
        setNoEventsState(false);
        eventsList.setCellFactory(param -> new ListCell<>() {
            private final ImageView profileImage = new ImageView(String.valueOf(App.class.getResource("images/defaultUserImage.png")));

            @Override
            public void updateItem(Event event, boolean empty) {
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
                    profileImage.setFitWidth(320);
                    profileImage.setFitHeight(180);
                    profileImage.setImage(new Image(event.getImagePath()));
                    vbox.setPadding(new Insets(15));

                    Text descriptionText = new Text(event.getDescription());
                    descriptionText.getStyleClass().add("fancyText");
                    TextFlow descriptionFlow = new TextFlow(descriptionText);
                    descriptionFlow.maxHeight(160);
                    Text date = new Text(event.getDate().format(eventDateTime));
                    date.getStyleClass().add("fancyText");
                    TextFlow dateFlow = new TextFlow(date);
                    Text title = new Text(event.getTitle());
                    title.getStyleClass().add("fancyTitle");
                    TextFlow titleFlow = new TextFlow(title);
                    vbox.getChildren().addAll(dateFlow, titleFlow, descriptionFlow);
                    vbox.setAlignment(Pos.CENTER_LEFT);
                    hbox.setPadding(new Insets(15));
                    hbox.getChildren().addAll(profileImage, vbox);

                    hbox.setMaxWidth(930);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(hbox);
                    if (isSelected()) {
                        setStyle("-fx-background-color: #1c2a36");
                    } else {
                        setStyle("-fx-background-color: #243142");
                    }
                }
            }
        });
        eventsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue==null){
                setNoEventsState(true);
                return;
            }
            setNoEventsState(false)
            updateModel();
            if(userAttendance.contains(newValue.getId())){
                leaveEventButton.setVisible(true);
            }
            if(userEventsWithNotifications.contains(newValue.getId())){
                noNotificationsImage.setImage(App.class.getResource("images/"));
            }
        });
    }

    /**
     * Initialize data using the service, and logged-in user
     *
     * @param service the Serchangevice
     * @param userId  the logged-in users' id
     */
    public void initData(Service service, Long userId) {
        this.service = service;
        this.userId = userId;
        updateModel();
        service.addObserver(this);
        eventsList.setItems(modelEvents);
    }

    /**
     * Update the data model with new data from the service.
     */
    public void updateModel() {
        userAttendance.clear();
        userEventsWithNotifications.clear();
        userEventsWithNotifications.clear();
        modelEvents.setAll(service.getEvents());
        service.getEventsForUser(userId)
                .stream()
                .forEach(x -> {
                    if (x.hasNotifications()) {
                        userEventsWithNotifications.add(x.getId().getKey());
                    }
                    userAttendance.add(x.getId().getKey());
                });
    }

    public void onJoinEventButtonClick() {

    }

    public void onLeaveEventButtonClick() {

    }

    public void onNoNotificationsButtonClick() {

    }

    public void onLogoutButtonClick() {
        App.changeSceneToLogin(service);
    }

    public void onHomeButtonClick() {
        App.changeSceneToMainWindow(service, userId);
    }

    public void onNotificationsButtonClick() {
        App.changeSceneToMainWindow(service, userId);
    }

    public void onNewEventButtonClick() {
        App.changeSceneToNewEventWindow(service, userId);
    }

    public void onNowClick() {
        App.changeSceneToNewEventWindow(service, userId);
    }

    @Override
    public void update(ChangeObserverEvent ChangeObserverEvent) {

    }
}
