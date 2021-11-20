package socialnetwork.repository;

/**
 * Repository Exception class which extends Runtime Exception
 */
public class RepositoryException extends RuntimeException{

    public RepositoryException(){
    }

    public RepositoryException(String message){
        super(message);
    }
}
