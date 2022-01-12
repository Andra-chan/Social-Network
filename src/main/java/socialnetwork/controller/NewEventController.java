package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import socialnetwork.App;
import socialnetwork.Util.events.ChangeEvent;
import socialnetwork.Util.observer.Observer;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.Service;

public class NewEventController implements Observer<ChangeEvent> {
    Service service;
    Long userId;
    ObservableList<Utilizator> modelNewEvent = FXCollections.observableArrayList();

    @FXML
    Button notificationsButton;

    @FXML
    Button homeButton;

    @FXML
    Button eventsButton;

    @FXML
    Button logoutButton;

    @FXML
    TextField eventNameTextField;

    @FXML
    DatePicker eventStartDatePicker;

    @FXML
    Spinner eventStartHourSpinner;

    @FXML
    Spinner eventStartMinuteSpinner;

    @FXML
    Button uploadImageButton;

    @FXML
    TextArea insertDescriptionTextArea;

    @FXML
    Button createEventButton;

    @FXML
    Label warningLabel;

    @FXML
    ImageView eventImage;

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

    public void onCreateEventButtonClick(){

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

    public void onEventsButtonClick(){
        App.changeSceneToEventsWindow(service, userId);
    }

    @Override
    public void update(ChangeEvent changeEvent) {

    }
}
