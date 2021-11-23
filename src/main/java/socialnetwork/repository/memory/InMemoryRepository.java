package socialnetwork.repository.memory;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import java.util.HashMap;
import java.util.Map;

/**
 * Repository that stores the information in memory while the program is running
 * @param <ID> a type E must have an attribute of type ID
 * @param <E> type of Entity saved in the repository
 */
public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    private Validator<E> validator;
    Map<ID, E> entities;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities = new HashMap<ID, E>();
    }

    /**
     * Finds and returns a user in the entity Map
     * @param id specific attribute of an entity
     * @return an user
     * @throws RepositoryException if the ID is null
     */
    @Override
    public E findOne(ID id){
        if (id == null)
            throw new RepositoryException("ID can't be null!");
        return entities.get(id);
    }

    /**
     *
     * @return all users in the Map
     */
    @Override
    public Iterable<E> findAll(){
        return entities.values();
    }

    /**
     * Method which overrides the Repository interface save()
     * saves an entity into the repository
     * @param entity to be saved
     * @return entity if it already exists, null otherwise
     * @throws RepositoryException if the given entity is null
     */
    @Override
    public E save(E entity){
        if (entity == null)
            throw new RepositoryException("Entity can't be null");
        validator.validate(entity);
        if(entities.get(entity.getId()) != null) {
            return entity;
        }
        else entities.put(entity.getId(), entity);
        return null;
    }

    /**
     * Method which overrides the Repository interface remove()
     * deletes an entity from the repository
     * @param id entity to be removed at the specified id
     * @return the removed entity, null otherwise(e.g. it doesn't exist)
     * @throws RepositoryException if the given ID is null
     */
    @Override
    public E delete(ID id){
        if(id == null)
            throw new RepositoryException("ID can't be null!");
        if(findOne(id) != null){
            entities.remove(id);
            return findOne(id);
        }
        return null;
    }

    /**
     * Method which overrides the Repository interface update()
     * updates an entity stored in the repository
     * @param entity to be updated
     * @return null if the entity was update, the entity otherwise(e.g. it doesn't exist)
     * @throws RepositoryException if the given entity is null
     */
    @Override
    public E update(E entity){
        if(entity == null)
            throw new RepositoryException("Entity can't be null!");
        validator.validate(entity);

        if(entities.get(entity.getId()) != null){
            entities.put(entity.getId(), entity);
            return null;
        }
        return entity;
    }
}


