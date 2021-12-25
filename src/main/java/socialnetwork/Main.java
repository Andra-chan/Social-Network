package socialnetwork;

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
import socialnetwork.ui.UserInterface;

/**
 * Main class
 * Starts the program, initiates the Repository, Validator and Service
 * Contains where the input will be stored(database)
 */
public class Main {

    public static void main(String[] args) {

        String url = "jdbc:postgresql://localhost:5432/socialnetwork";
        String username = "postgres";
        String password = "mypostgres";

        try {
            Validator<Utilizator> userValidator = new UtilizatorValidator();

            Repository<Long, Utilizator> userRepository = new UtilizatorDbRepository(url, username, password,
                    userValidator);

            Repository<Long, Prietenie> friendshipRepository = new FriendshipDbRepository(url, username, password);

            MessageDbRepository messageRepo = new MessageDbRepository(url, username, password, new MessageValidator());

            Repository<Long, FriendRequest> friendRequestRepository = new FriendRequestDbRepository(url, username,
                    password);

            Service serviceNetwork = new Service(userRepository, friendshipRepository, friendRequestRepository,
                    messageRepo);

            UserInterface ui = new UserInterface(serviceNetwork);
            ui.run();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
