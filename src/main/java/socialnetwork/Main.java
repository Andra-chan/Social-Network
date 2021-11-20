import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.FriendshipDbRepository;
import socialnetwork.repository.database.UtilizatorDbRepository;
import socialnetwork.repository.file.FriendshipFile;
import socialnetwork.repository.file.UtilizatorFile;
import socialnetwork.service.Service;
import socialnetwork.ui.UserInterface;

/**
 * Main class
 * Starts the program, initiates the Repository, Validator and Service
 * Contains where the input will be stored(csv file for now)
 */
public class Main{

    private UserInterface ui;

    public void setUI(UserInterface ui){
        this.ui = ui;
    }

    public static void main(String[] args){

        String url = "jdbc:postgresql://localhost:5432/Utilizatori";
        String username = "postgres";
        String password = "amihaila0727";
        try {
            Validator<Utilizator> userValidator = new UtilizatorValidator();
            //Repository<Long, Utilizator> userRepository = new UtilizatorFile("data/users.csv", userValidator);
            Repository<Long, Utilizator> userRepository = new UtilizatorDbRepository(url, username, password, userValidator);

            Validator<Prietenie> friendshipValidator = new FriendshipValidator();
            //Repository<Long, Prietenie> friendshipRepository = new FriendshipFile("data/friendships.csv", friendshipValidator);
            Repository<Long, Prietenie> friendshipRepository = new FriendshipDbRepository(url, username, password);

            Service service = new Service(userRepository, friendshipRepository);

            UserInterface ui = new UserInterface(service);
            ui.run();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
