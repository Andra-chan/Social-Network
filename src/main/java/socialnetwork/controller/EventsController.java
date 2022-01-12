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
import javafx.scene.paint.Color;
import socialnetwork.App;
import socialnetwork.Util.events.ChangeEvent;
import socialnetwork.Util.observer.Observer;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.Service;

import java.net.ContentHandlerFactory;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static socialnetwork.Util.imageHelper.Helpers.setProfileImage;

public class EventsController implements Observer<ChangeEvent> {
    Service service;
    Long userId;
    ObservableList<Utilizator> modelEvents = FXCollections.observableArrayList();

    @FXML
    Button notificationsButton;

    @FXML
    Button homeButton;

    @FXML
    Button logoutButton;

    @FXML
    Button newEventButton;

    @FXML
    ListView eventsList;

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
    public void initialize(){

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
    }

    /**
     * Update the data model with new data from the service.
     */
    public void updateModel(){

    }

    public void onJoinEventButtonClick(){

    }

    public void onLeaveEventButtonClick(){

    }

    public void onNoNotificationsButtonClick(){

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

    public void onNewEventButtonClick(){
        App.changeSceneToNewEventWindow(service, userId);
    }

    public void onNowClick(){
        App.changeSceneToNewEventWindow(service, userId);
    }

    @Override
    public void update(ChangeEvent changeEvent) {

    }
}
