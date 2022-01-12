package socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import socialnetwork.controller.*;
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.UserCredentials;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.*;
import socialnetwork.service.Service;

import java.io.IOException;

public class App extends Application {
    public static Stage currentStage;
    public static String defaultImagePath;

    @Override
    public void start(Stage stage) throws IOException {
        App.currentStage = stage;

        String url = "jdbc:postgresql://localhost:5432/socialnetwork";
        String username = "postgres";
        String password = "mypostgres";

        Validator<Utilizator> userValidator = new UtilizatorValidator();

        Repository<Long, Utilizator> userRepository = new UtilizatorDbRepository(url, username, password,
                userValidator);

        Repository<String, UserCredentials> userCredentialsRepository = new UserCredentialsDbRepository(url, username,
                password);

        Repository<Long, Prietenie> friendshipRepository = new FriendshipDbRepository(url, username, password);

        MessageDbRepository messageRepo = new MessageDbRepository(url, username, password, new MessageValidator());

        Repository<Long, FriendRequest> friendRequestRepository = new FriendRequestDbRepository(url, username,
                password);

        Service serviceNetwork = new Service(userRepository, userCredentialsRepository, friendshipRepository,
                friendRequestRepository,
                messageRepo);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        // HBox root = fxmlLoader.load();
        BorderPane root = fxmlLoader.load();
        // ApplicationController controller = fxmlLoader.getController();

        LoginController controller = fxmlLoader.getController();
        controller.setService(serviceNetwork);

        // controller.setService(serviceNetwork);

        Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());

        defaultImagePath = String.valueOf(getClass().getResource("images/defaultUserImage.png"));

        stage.setTitle("Starfall");
        stage.setScene(scene);
        stage.setMinHeight(root.getPrefHeight());
        stage.setMinWidth(root.getPrefWidth());
        stage.setMaxHeight(root.getPrefHeight());
        stage.setMaxWidth(root.getPrefWidth());
        stage.setResizable(false);
        stage.show();
    }

    public static void changeSceneToMainWindow(Service service, Long userId) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("mainPage.fxml"));
            AnchorPane root = fxmlLoader.load();
            App.currentStage.setScene(new Scene(root, root.getPrefWidth(), root.getPrefHeight()));
            App.currentStage.setMinHeight(root.getPrefHeight());
            App.currentStage.setMinWidth(root.getPrefWidth());
            App.currentStage.setResizable(false);
            service.clearObservers();

            MainPageController controller = fxmlLoader.getController();
            controller.initData(service, userId);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void changeSceneToFriendsWindow(Service service, Long userId) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("friendsPage.fxml"));
            AnchorPane root = fxmlLoader.load();
            App.currentStage.setScene(new Scene(root, root.getPrefWidth(), root.getPrefHeight()));
            App.currentStage.setMinHeight(root.getPrefHeight());
            App.currentStage.setMinWidth(root.getPrefWidth());
            App.currentStage.setResizable(false);
            service.clearObservers();

            FriendsController controller = fxmlLoader.getController();
            controller.initData(service, userId);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void changeSceneToFriendRequestsWindow(Service service, Long userId) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("friendRequestsPage.fxml"));
            AnchorPane root = fxmlLoader.load();
            App.currentStage.setScene(new Scene(root, root.getPrefWidth(), root.getPrefHeight()));
            App.currentStage.setMinHeight(root.getPrefHeight());
            App.currentStage.setMinWidth(root.getPrefWidth());
            App.currentStage.setResizable(false);
            service.clearObservers();

            FriendRequestsController controller = fxmlLoader.getController();
            controller.initData(service, userId);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void changeSceneToAddFriendsWindow(Service service, Long userId) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("addFriendsPage.fxml"));
            AnchorPane root = fxmlLoader.load();
            App.currentStage.setScene(new Scene(root, root.getPrefWidth(), root.getPrefHeight()));
            App.currentStage.setMinHeight(root.getPrefHeight());
            App.currentStage.setMinWidth(root.getPrefWidth());
            App.currentStage.setResizable(false);
            service.clearObservers();

            AddFriendsController controller = fxmlLoader.getController();
            controller.initData(service, userId);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void changeSceneToMessagesWindow(Service service, Long userId) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("messagesPage.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
            scene.getStylesheets().add(String.valueOf(App.class.getResource("css/style.css")));

            App.currentStage.setScene(scene);
            App.currentStage.setMinHeight(root.getPrefHeight());
            App.currentStage.setMinWidth(root.getPrefWidth());
            App.currentStage.setResizable(false);
            service.clearObservers();

            MessagesController controller = fxmlLoader.getController();
            controller.initData(service, userId);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void changeSceneToSettingsWindow(Service service, Long userId) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("settingsPage.fxml"));
            AnchorPane root = fxmlLoader.load();
            App.currentStage.setScene(new Scene(root, root.getPrefWidth(), root.getPrefHeight()));
            App.currentStage.setMinHeight(root.getPrefHeight());
            App.currentStage.setMinWidth(root.getPrefWidth());
            App.currentStage.setResizable(false);
            service.clearObservers();

            SettingsController controller = fxmlLoader.getController();
            controller.initData(service, userId);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void changeSceneToEventsWindow(Service service, Long userId) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("eventsPage.fxml"));
            AnchorPane root = fxmlLoader.load();
            App.currentStage.setScene(new Scene(root, root.getPrefWidth(), root.getPrefHeight()));
            App.currentStage.setMinHeight(root.getPrefHeight());
            App.currentStage.setMinWidth(root.getPrefWidth());
            App.currentStage.setResizable(false);
            service.clearObservers();

            EventsController controller = fxmlLoader.getController();
            controller.initData(service, userId);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void changeSceneToNewEventWindow(Service service, Long userId) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("newEventPage.fxml"));
            AnchorPane root = fxmlLoader.load();
            App.currentStage.setScene(new Scene(root, root.getPrefWidth(), root.getPrefHeight()));
            App.currentStage.setMinHeight(root.getPrefHeight());
            App.currentStage.setMinWidth(root.getPrefWidth());
            App.currentStage.setResizable(false);
            service.clearObservers();

            NewEventController controller = fxmlLoader.getController();
            controller.initData(service, userId);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void changeSceneToRegister(Service service) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("register.fxml"));
            AnchorPane root = fxmlLoader.load();
            App.currentStage.setScene(new Scene(root, root.getPrefWidth(), root.getPrefHeight()));
            App.currentStage.setMinHeight(root.getPrefHeight());
            App.currentStage.setMinWidth(root.getPrefWidth());
            App.currentStage.setResizable(false);
            service.clearObservers();

            RegisterController controller = fxmlLoader.getController();
            controller.setService(service);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void changeSceneToLogin(Service service) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("login.fxml"));
            BorderPane root = fxmlLoader.load();
            App.currentStage.setScene(new Scene(root, root.getPrefWidth(), root.getPrefHeight()));
            App.currentStage.setMinHeight(root.getPrefHeight());
            App.currentStage.setMinWidth(root.getPrefWidth());
            App.currentStage.setResizable(false);
            App.currentStage.setMaximized(false);
            service.clearObservers();

            LoginController controller = fxmlLoader.getController();
            controller.setService(service);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch();
    }
}
