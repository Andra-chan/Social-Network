package socialnetwork.repository.file;

import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.validators.Validator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

public class FriendRequestFile extends AbstractFileRepository<Long, FriendRequest> {


    public FriendRequestFile(Validator<FriendRequest> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    public FriendRequest extractEntity(List<String> attributes){
        FriendRequest friendRequest = new FriendRequest(
                Long.parseLong(attributes.get(1)),
                Long.parseLong(attributes.get(2)));
        friendRequest.setId(Long.parseLong(attributes.get(0)));
        friendRequest.setStatus(attributes.get(4));
        friendRequest.setLocalDateTime(LocalDateTime.parse(attributes.get(3)));
        return friendRequest;
    }

    @Override
    protected String createEntityAsString(FriendRequest entity){
        return entity.getId() + ";" + entity.getSender() + ";" + entity.getReceiver()  + ";" + entity.getLocalDateTime() + ";" + entity.getStatus();
    }

    @Override
    public FriendRequest delete(Long id){

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
