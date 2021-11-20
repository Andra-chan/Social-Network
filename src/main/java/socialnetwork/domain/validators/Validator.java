package socialnetwork.domain.validators;

/**
 * Validator interface with validate() method to be overriten
 * @param <T>
 */
public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}
