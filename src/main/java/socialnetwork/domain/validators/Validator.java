package socialnetwork.domain.validators;

/**
 * Validator interface with validate() method to be overwritten
 * @param <T>
 */
public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}
