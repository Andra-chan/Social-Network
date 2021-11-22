package socialnetwork;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.FriendshipDbRepository;
import socialnetwork.repository.database.MessageDbRepository;
import socialnetwork.repository.database.UtilizatorDbRepository;
import socialnetwork.service.Service;
import socialnetwork.ui.UserInterface;

/**
 * Main class
 * Starts the program, initiates the Repository, Validator and Service
 * Contains where the input will be stored(csv file for now)
 */
public class Main {

    public static void main(String[] args) {

        String url = "jdbc:postgresql://localhost:5432/socialnetwork";
        String username = "postgres";
        String password = "admin";
        try {
            Validator<Utilizator> userValidator = new UtilizatorValidator();

            Repository<Long, Utilizator> userRepository = new UtilizatorDbRepository(url, username, password,
                    userValidator);

            Repository<Long, Prietenie> friendshipRepository = new FriendshipDbRepository(url, username, password);

            MessageDbRepository messageRepo = new MessageDbRepository(url, username, password);
            var messages = messageRepo.findAll();
            messages.forEach(x -> System.out.println(x));

            Service service = new Service(userRepository, friendshipRepository, messageRepo);
            service.getAllMessagesBetweenTwoUsers(2l, 7l);

            UserInterface ui = new UserInterface(service);
            ui.run();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
