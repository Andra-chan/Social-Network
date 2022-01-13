package socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import socialnetwork.App;
import socialnetwork.Util.controller.NotificationService;
import socialnetwork.service.Service;

public class ReportsController {
    Service service;
    NotificationService notificationService;
    Long userId;

    @FXML
    Button notificationsButton;

    @FXML
    Button homeButton;

    @FXML
    Button logoutButton;

    @FXML
    Button eventsButton;

    @FXML
    ImageView notificationButtonImage;

    @FXML
    public void initialize() {

    }

    public void initData(Service service, Long userId) {
        this.service = service;
        this.userId = userId;
        notificationService = new NotificationService(service, userId, notificationsButton,
                notificationButtonImage, String.valueOf(App.class.getResource("images/notificationsImage.png")),
                String.valueOf(App.class.getResource("images/activeNotifications.png")));
        notificationService.setPeriod(Duration.seconds(5));
        notificationService.start();
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

    public void onEventsButtonClick(){App.changeSceneToEventsWindow(service, userId);}
}
