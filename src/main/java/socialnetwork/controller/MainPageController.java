package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import socialnetwork.App;
import socialnetwork.Util.controller.EventCell;
import socialnetwork.Util.controller.NotificationService;
import socialnetwork.domain.Event;
import socialnetwork.domain.EventAttendance;
import socialnetwork.service.Service;
import socialnetwork.service.paging.Page;
import socialnetwork.service.paging.PageableImplementation;

import java.util.List;
import java.util.stream.Collectors;

import static socialnetwork.Util.imageHelper.Helpers.setProfileImage;

public class MainPageController {
    Service service;
    Long userId;
    NotificationService notificationService;
    ObservableList<Event> modelEvents = FXCollections.observableArrayList();

    int currentPage = 0;
    final int pageSize = 4;

    @FXML
    Label usernameLabel;

    @FXML
    ImageView userImageView;

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
    Button notificationsButton;

    @FXML
    Button eventsButton;

    @FXML
    Label nowLabel;

    @FXML
    Label noUpcomingEventsLabel;

    @FXML
    ImageView noUpcomingEventsImage;

    @FXML
    Label upcomingEventsLabel;

    @FXML
    ListView<Event> upcomingEventsList;

    @FXML
    Button reportsButton;

    @FXML
    ImageView notificationButtonImage;

    @FXML
    Button prevPage;

    @FXML
    Button nextPage;

    private void setNoEventsState(boolean state) {
        upcomingEventsList.setVisible(!state);
        upcomingEventsLabel.setVisible(!state);
        noUpcomingEventsImage.setVisible(state);
        noUpcomingEventsLabel.setVisible(state);
        nowLabel.setVisible(state);
        nextPage.setVisible(!state);
        prevPage.setVisible(!state);
    }

    @FXML
    public void initialize() {
        setNoEventsState(true);
        upcomingEventsList.setItems(modelEvents);
        upcomingEventsList.setCellFactory(x -> new EventCell(160, 90, 400, false));
    }

    public List<Event> getEvents() {
        return service.getUpcomingEventsForUserPaged(userId, new PageableImplementation(currentPage, pageSize)).stream()
                .map(EventAttendance::getEvent)
                .toList();
    }

    public void updateModel() {
        modelEvents.setAll(getEvents());
        setNoEventsState(modelEvents.size() == 0);

    }

    /**
     * Initialize the data using the service and the logged-in user.
     *
     * @param service the Service
     * @param userId  logged-in users' id
     */
    public void initData(Service service, Long userId) {
        this.service = service;
        this.userId = userId;
        var user = service.getUser(userId);
        usernameLabel.setText(user.getFirstName() + " " + user.getLastName());
        setProfileImage(user, userImageView);
        notificationService = new NotificationService(service, userId, notificationsButton,
                notificationButtonImage, String.valueOf(App.class.getResource("images/notificationsImage.png")),
                String.valueOf(App.class.getResource("images/activeNotifications.png")));
        notificationService.setPeriod(Duration.seconds(5));
        notificationService.start();
        updateModel();
    }

    public void onPreviousPageButtonClick() {
        if(currentPage==0){
            return;
        }
        currentPage--;
        updateModel();
    }

    public void onNextPageButtonClick() {
        currentPage++;
        var events = getEvents();
        if(events.isEmpty()){
            currentPage--;
            return;
        }
        modelEvents.setAll(events);
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

    /**
     * When the user clicks on the add friendsbutton, switch the scene
     */
    public void onMenuAddFriendsClick() {
        App.changeSceneToAddFriendsWindow(service, userId);
    }

    /**
     * When the user clicks on the messages button, switch the scene
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
     * When the user clicks on the logout button, switch the scene
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

    public void onNowClick() {
        App.changeSceneToEventsWindow(service, userId);
    }

    public void onReportsButtonClick() {
        App.changeSceneToReportsWindow(service, userId);
    }
}
