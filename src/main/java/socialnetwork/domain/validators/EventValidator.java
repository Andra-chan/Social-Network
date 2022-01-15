package socialnetwork.domain.validators;

import socialnetwork.domain.Event;
import socialnetwork.domain.FriendRequest;

public class EventValidator implements Validator<Event> {

    /**
     * Validates a FriendRequest
     * Exceptions:
     * - no current exceptions
     */
    @Override
    public void validate(Event entity) throws ValidationException {
        if(entity.getDescription().isBlank()){
            throw new ValidationException("Event description can't be blank");
        }
        if(entity.getTitle().isBlank()){
            throw new ValidationException("Event title can't be blank");
        }
        if(entity.getImage_path().isBlank()){
            throw new ValidationException("The event requires an image");
        }
    }
}