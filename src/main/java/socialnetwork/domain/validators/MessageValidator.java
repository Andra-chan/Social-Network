package socialnetwork.domain.validators;

import socialnetwork.domain.Message;

public class MessageValidator implements Validator<Message> {

    @Override
    public void validate(Message entity) throws ValidationException {
        if (entity == null) {
            throw new ValidationException("Message can't be null");
        }
        if (entity.getMessageBody().length() == 0) {
            throw new ValidationException("Message body can't be empty");
        }
        if (entity.getDate() == null) {
            throw new ValidationException("Message date can't be null");
        }
        if (entity.getId() == null) {
            throw new ValidationException("Message id can't be null");
        }
        if (entity.getTo() == null) {
            throw new ValidationException("Message recipient list can't be null");
        }
    }
}
