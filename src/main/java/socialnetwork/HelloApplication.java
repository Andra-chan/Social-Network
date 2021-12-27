package socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import socialnetwork.controller.ApplicationController;
import socialnetwork.controller.ChatboxController;
import socialnetwork.controller.LoginController;
import socialnetwork.controller.RegisterController;
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

public class HelloApplication extends Application {
    private static Stage currentStage;

    @Override
    public void start(Stage stage) throws IOException {
        HelloApplication.currentStage = stage;

        String url = "jdbc:postgresql://localhost:5432/socialnetwork";
        String username = "postgres";
        String password = "admin";

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

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Chatbox.fxml"));
        // HBox root = fxmlLoader.load();
        AnchorPane root = fxmlLoader.load();
        // ApplicationController controller = fxmlLoader.getController();

        ChatboxController controller = fxmlLoader.getController();

        // controller.setService(serviceNetwork);

        Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
        scene.getStylesheets().add(String.valueOf(getClass().getResource("css/style.css")));
        controller.setService(serviceNetwork, 7l, 9l);

        stage.setTitle("Social Network");
        stage.setScene(scene);
        stage.setMinHeight(root.getPrefHeight());
        stage.setMinWidth(root.getPrefWidth());
        stage.show();
    }

    public static void changeSceneToMainWindow(Service service, Long userId) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            HBox root = fxmlLoader.load();
            HelloApplication.currentStage.setScene(new Scene(root, root.getPrefWidth(), root.getPrefHeight()));
            HelloApplication.currentStage.setMinHeight(root.getPrefHeight());
            HelloApplication.currentStage.setMinWidth(root.getPrefWidth());
            ApplicationController controller = fxmlLoader.getController();
            controller.initData(service, userId);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void changeSceneToRegister(Service service) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("register.fxml"));
            AnchorPane root = fxmlLoader.load();
            HelloApplication.currentStage.setScene(new Scene(root, root.getPrefWidth(), root.getPrefHeight()));
            HelloApplication.currentStage.setMinHeight(root.getPrefHeight());
            HelloApplication.currentStage.setMinWidth(root.getPrefWidth());
            RegisterController controller = fxmlLoader.getController();
            controller.setService(service);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void changeSceneToLogin(Service service) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
            AnchorPane root = fxmlLoader.load();
            HelloApplication.currentStage.setScene(new Scene(root, root.getPrefWidth(), root.getPrefHeight()));
            HelloApplication.currentStage.setMinHeight(root.getPrefHeight());
            HelloApplication.currentStage.setMinWidth(root.getPrefWidth());
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
