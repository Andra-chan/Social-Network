package socialnetwork.domain.validators;

import org.apache.commons.validator.routines.EmailValidator;

import socialnetwork.domain.Utilizator;

/**
 * Validates an Utilizator
 * Exceptions:
 * - the friendship of a user cannot be null
 * - first name and last name can contain only letters
 */
public class UtilizatorValidator implements Validator<Utilizator> {

    @Override
    public void validate(Utilizator entity) throws ValidationException {
        if (entity == null)
            throw new ValidationException("User cannot be null!");
        if (!entity.getFirstName().matches("[a-zA-Z]*"))
            throw new ValidationException("First name should contain only letters");
        if (!entity.getLastName().matches("[a-zA-Z]*"))
            throw new ValidationException("Last name should contain only letters");
        EmailValidator emailValidator = EmailValidator.getInstance();
        if (!emailValidator.isValid(entity.getEmail()))
            throw new ValidationException("Invalid email");
        if (entity.getPassword().isBlank())
            throw new ValidationException("Password can't be blank");
    }
}
