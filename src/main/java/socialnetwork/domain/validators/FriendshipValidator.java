package socialnetwork.domain.validators;

import socialnetwork.domain.Prietenie;

/**
 * Validates a Friendship
 * Exceptions:
 * - given user cannot be null
 * - first user ID  and second user ID must only contain digits
 */
public class FriendshipValidator implements Validator<Prietenie> {

    @Override
    public void validate(Prietenie entity) throws ValidationException{
        if(entity == null)
            throw new ValidationException("User cannot be null!");
        if(!entity.getFirstUser().toString().matches("[0-9]*"))
            throw new ValidationException("First user ID must contain only digits");
        if(!entity.getSecondUser().toString().matches("[0-9]*"))
            throw new ValidationException("Second user ID must contain only digits");
    }
}
