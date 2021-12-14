package socialnetwork.repository.file;

import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.validators.Validator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

/**
 * FriendRequest File Repository
 */
public class FriendRequestFile extends AbstractFileRepository<Long, FriendRequest> {


    public FriendRequestFile(Validator<FriendRequest> validator, String fileName) {
        super(validator, fileName);
    }

    /**
     * extract entity override for friendRequest  - template method design pattern
     * creates an entity of type FriendRequest having a specified list of @code attributes
     *
     * @param attributes
     * @return an entity of type FriendRequest
     */
    @Override
    public FriendRequest extractEntity(List<String> attributes) {
        FriendRequest friendRequest = new FriendRequest(
                Long.parseLong(attributes.get(1)),
                Long.parseLong(attributes.get(2)));
        friendRequest.setId(Long.parseLong(attributes.get(0)));
        friendRequest.setStatus(attributes.get(4));
        friendRequest.setLocalDateTime(LocalDateTime.parse(attributes.get(3)));
        return friendRequest;
    }

    @Override
    protected String createEntityAsString(FriendRequest entity) {
        return entity.getId() + ";" + entity.getSender() + ";" + entity.getReceiver() + ";" + entity.getLocalDateTime() + ";" + entity.getStatus();
    }

    /**
     * Overwritten method of delete in order to update the status to REJECTED and not actually delete the FriendRequest
     *
     * @param id entity to be removed at the specified id
     * @return
     */
    @Override
    public FriendRequest delete(Long id) {

        FriendRequest request = super.findOne(id);
        request.setStatus("REFUSED");
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(fileName);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        assert pw != null;
        pw.close();
        this.findAll().forEach(this::writeToFile);
        return request;
    }
}
