package socialnetwork.repository.file;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.validators.Validator;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Friendship File Repository
 */
public class FriendshipFile extends AbstractFileRepository<Long, Prietenie> {

    public FriendshipFile(String fileName, Validator<Prietenie> validator) {
        super(validator, fileName);
    }

    /**
     * extract entity override for friendship  - template method design pattern
     * creates an entity of type Prietenie having a specified list of @code attributes
     *
     * @param attributes
     * @return an entity of type Prietenie
     */
    @Override
    public Prietenie extractEntity(List<String> attributes) {
        Prietenie friendship = new Prietenie(
                Long.parseLong(attributes.get(1)),
                Long.parseLong(attributes.get(2)),
                LocalDateTime.parse(attributes.get(3)));
        friendship.setId(Long.parseLong(attributes.get(0)));

        return friendship;
    }

    @Override
    protected String createEntityAsString(Prietenie entity) {
        return entity.getId() + ";" + entity.getFirstUser() + ";" + entity.getSecondUser() + ";" + entity.getDate();
    }
}
