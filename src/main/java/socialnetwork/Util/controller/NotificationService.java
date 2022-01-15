package socialnetwork.Util.controller;

import java.time.LocalDateTime;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import socialnetwork.service.Service;

public class NotificationService extends ScheduledService<Void> {
    Long currentUser;
    Service service;
    Button notificationButton;
    ImageView notificationImageView;
    Image notificationImage, noNotificationsImage;

    public NotificationService(Service service, Long currentUser,
                               Button notificationButton, ImageView notificationImageView,
                               String imageState1, String imageState2) {
        this.service = service;
        this.currentUser = currentUser;
        this.notificationButton = notificationButton;
        this.notificationImageView = notificationImageView;
        this.noNotificationsImage = new Image(imageState1);
        this.notificationImage = new Image(imageState2);
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                var eventCount = service.getUpcomingEventsForUser(currentUser).stream()
                        .filter(x-> x.hasNotifications())
                        .count();
                if (eventCount != 0) {
                    Glow glow = new Glow();
                    glow.setLevel(1.0);
                    notificationButton.setEffect(glow);
                    notificationImageView.setImage(notificationImage);
                } else {
                    notificationButton.setEffect(new DropShadow());
                    notificationImageView.setImage(noNotificationsImage);
                }
                return null;
            }
        };
    }
}
