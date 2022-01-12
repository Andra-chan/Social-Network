package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import socialnetwork.App;
import socialnetwork.Util.events.ChangeObserverEvent;
import socialnetwork.Util.observer.Observer;
import socialnetwork.domain.Event;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class NewEventController{
    Service service;
    Long userId;
    String currentImageURL;

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
    Spinner<Integer> eventStartHourSpinner;

    @FXML
    Spinner<Integer> eventStartMinuteSpinner;

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
        eventStartHourSpinner.valueFactoryProperty().setValue(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0, 1));
        eventStartMinuteSpinner.valueFactoryProperty().setValue(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0, 1));
    }

    /**
     * Initialize data using the service, and logged-in user
     * @param service the Service
     * @param userId the logged-in users' id
     */
    public void initData(Service service, Long userId) {
        this.service=service;
        this.userId=userId;
        currentImageURL="";
    }


    public void onCreateEventButtonClick(){
        String title =  eventNameTextField.getText();
        LocalDate date = eventStartDatePicker.getValue();
        int hour = eventStartHourSpinner.getValue();
        int minute = eventStartMinuteSpinner.getValue();
        LocalTime time = LocalTime.of(hour, minute);
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        LocalDateTime now = LocalDateTime.now();
        if(dateTime.isBefore(now)){
            warningLabel.setVisible(true);
            warningLabel.setText("Invalid event time");
            return;
        }
        String description = insertDescriptionTextArea.getText();
        if(currentImageURL.isBlank()){
            warningLabel.setVisible(true);
            warningLabel.setText("Invalid image");
            return;
        }
        try {
            service.addEvent(new Event(currentImageURL, title, description, dateTime));
        }catch(Exception ex){
            warningLabel.setVisible(true);
            warningLabel.setText(ex.getMessage());
            return;
        }
    }

    private void updateImage(){
        eventImage.setImage(new Image(currentImageURL));
    }

    public void onUploadImageButtonClick(){
        FileChooser choose = new FileChooser();
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG", "*.JPEG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        choose.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);
        File file = choose.showOpenDialog(null);
        if (file == null) {
            return;
        }
        try {
            currentImageURL = file.toURI().toURL().toExternalForm();
            updateImage();
            eventImage.setBlendMode(BlendMode.SRC_OVER);
        } catch (Exception ex) {
            ex.printStackTrace();
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

    public void onEventsButtonClick(){
        App.changeSceneToEventsWindow(service, userId);
    }

}
