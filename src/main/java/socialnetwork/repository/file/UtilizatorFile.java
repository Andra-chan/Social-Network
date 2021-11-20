package socialnetwork.repository.file;

import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;

import java.util.List;

/**
 * Utilizator File Repository
 */
public class UtilizatorFile extends  AbstractFileRepository<Long, Utilizator> {


    public UtilizatorFile(String validator, Validator<Utilizator> fileName) {
        super(fileName, validator);
    }

    /**
     *  extract entity  - template method design pattern
     *  creates an entity of type Utilizator having a specified list of @code attributes
     * @param attributes
     * @return an entity of type Utilizator
     */
    @Override
    public Utilizator extractEntity(List<String> attributes){
        Utilizator user = new Utilizator(attributes.get(1), attributes.get(2));
        user.setId(Long.parseLong(attributes.get(0)));

        return user;
    }

    @Override
    protected String createEntityAsString(Utilizator entity){
        return entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName();
    }
}
