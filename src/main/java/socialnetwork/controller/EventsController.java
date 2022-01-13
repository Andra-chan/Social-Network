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
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import socialnetwork.App;
import socialnetwork.Util.controller.EventCell;
import socialnetwork.Util.controller.NotificationService;
import socialnetwork.Util.events.ChangeObserverEvent;
import socialnetwork.Util.events.ChangeObserverEventType;
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
    NotificationService notificationService;

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

    @FXML
    Button noNotificationsButton;

    @FXML
    Button reportsButton;

    @FXML
    ImageView notificationButtonImage;

    public EventsController() {
        userAttendance = new HashSet<>();
        userEventsWithNotifications = new HashSet<>();
    }

    private void setNoEventsState(boolean state) {
        eventsList.setVisible(!state);
        noEventsImage.setVisible(state);
        noEventsLabel.setVisible(state);
        nowLabel.setVisible(state);
    }

    @FXML
    public void initialize() {
        setNoEventsState(true);
        noNotificationsButton.setVisible(false);
        eventsList.setCellFactory(param -> new EventCell(320, 180, 930, true));
        eventsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            reloadEventStatus();
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
        setNoEventsState(modelEvents.size() == 0);
        notificationService = new NotificationService(service, userId, notificationsButton,
                notificationButtonImage, String.valueOf(App.class.getResource("images/notificationsImage.png")),
                String.valueOf(App.class.getResource("images/activeNotifications.png")));
        notificationService.setPeriod(Duration.seconds(5));
        notificationService.start();
    }

    /**
     * Update the data model with new data from the service.
     */
    public void updateModel() {
        modelEvents.setAll(service.getEvents());
        updateCache();
    }

    private void reloadEventStatus(){
        var selectedEvent = eventsList.getSelectionModel().getSelectedItem();
        if (selectedEvent == null) {
            setNoEventsState(true);
            return;
        }
        setNoEventsState(false);
        if (userAttendance.contains(selectedEvent.getId())) {
            leaveEventButton.setVisible(true);
            joinEventButton.setVisible(false);
            noNotificationsButton.setVisible(true);
        } else {
            noNotificationsButton.setVisible(false);
            leaveEventButton.setVisible(false);
            joinEventButton.setVisible(true);
            return;
        }
        if (userEventsWithNotifications.contains(selectedEvent.getId())) {
            noNotificationsImage.setImage(new Image(String.valueOf(App.class.getResource("images/notificationsImage.png"))));
            noNotificationsButton.setEffect(new Glow());
        } else {
            noNotificationsImage.setImage(new Image(String.valueOf(App.class.getResource("images/noNotificationsImage.png"))));
            noNotificationsButton.setEffect(new DropShadow());
        }
    }

    private void updateCache(){
        userAttendance.clear();
        userEventsWithNotifications.clear();
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
        var selectedEvent = eventsList.getSelectionModel().getSelectedItem();
        if (!userAttendance.contains(selectedEvent.getId())) {
            service.addAttendance(selectedEvent.getId(), userId);
        }
    }

    public void onLeaveEventButtonClick() {
        var selectedEvent = eventsList.getSelectionModel().getSelectedItem();
        if (userAttendance.contains(selectedEvent.getId())) {
            service.cancelAttendance(selectedEvent.getId(), userId);
        }
    }

    public void onNoNotificationsButtonClick() {
        var selectedEvent = eventsList.getSelectionModel().getSelectedItem();
        if (userAttendance.contains(selectedEvent.getId())) {
            service.setNotificationsForEvent(userId, selectedEvent.getId(), !userEventsWithNotifications.contains(selectedEvent.getId()));
        }
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

    public void onReportsButtonClick(){
        App.changeSceneToReportsWindow(service, userId);
    }

    @Override
    public void update(ChangeObserverEvent event) {
        if(event.getType().equals(ChangeObserverEventType.EVENT_ATTENDANCE)){
            updateCache();
            reloadEventStatus();
        }
    }
}
