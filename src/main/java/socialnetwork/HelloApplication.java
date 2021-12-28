package socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.FriendRequestDbRepository;
import socialnetwork.repository.database.FriendshipDbRepository;
import socialnetwork.repository.database.MessageDbRepository;
import socialnetwork.repository.database.UtilizatorDbRepository;
import socialnetwork.service.Service;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        String url = "jdbc:postgresql://localhost:5432/socialnetwork";
        String username = "postgres";
        String password = "mypostgres";

        Validator<Utilizator> userValidator = new UtilizatorValidator();

        Repository<Long, Utilizator> userRepository = new UtilizatorDbRepository(url, username, password,
                userValidator);

        Repository<Long, Prietenie> friendshipRepository = new FriendshipDbRepository(url, username, password);

        MessageDbRepository messageRepo = new MessageDbRepository(url, username, password, new MessageValidator());

        Repository<Long, FriendRequest> friendRequestRepository = new FriendRequestDbRepository(url, username,
                password);

        Service serviceNetwork = new Service(userRepository, friendshipRepository, friendRequestRepository,
                messageRepo);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        HBox root = fxmlLoader.load();
        ApplicationController controller = fxmlLoader.getController();

        controller.setService(serviceNetwork);

        Scene scene = new Scene(root, 900, 450);
        stage.setTitle("Social Network");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}