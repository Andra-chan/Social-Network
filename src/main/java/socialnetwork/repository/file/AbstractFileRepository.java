package socialnetwork.repository.file;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.RepositoryException;
import socialnetwork.repository.memory.InMemoryRepository;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Template Method Design Pattern for Repository
 * @param <ID> each entity has an attribute of type ID
 * @param <E> entities stored in the Repository
 */
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    String fileName;

    public AbstractFileRepository(Validator<E> validator, String fileName) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    /**
     * loads data into the file
     */
    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String linie;
            while ((linie = br.readLine()) != null) {
                List<String> attr = Arrays.asList(linie.split(";"));
                E e = extractEntity(attr);
                super.save(e);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  extract entity  - template method design pattern
     *  creates an entity of type E having a specified list of @code attributes
     * @param attributes
     * @return an entity of type E
     */
    public abstract E extractEntity(List<String> attributes);

    protected abstract String createEntityAsString(E entity);

    /**
     * writes to file the given entity
     * @param entity to be added, if valid
     */
    protected void writeToFile(E entity){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))){
            bw.write(createEntityAsString(entity));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method which overrides the Repository interface save()
     * saves an entity into the repository and writes it to the file
     * @param entity to be saved
     * @return entity if it already exists, null otherwise
     * @throws RepositoryException if the given entity is null
     */
    @Override
    public E save(E entity){
        E e = super.save(entity);
        if(e==null)
            writeToFile(entity);
        return e;
    }

    /**
     * Method which overrides the Repository interface remove()
     * deletes an entity from the repository and updates the file
     * @param id entity to be removed at the specified id
     * @return the removed entity, null otherwise(e.g. it doesn't exist)
     * @throws RepositoryException if the given ID is null
     * @throws FileNotFoundException if the file does not exist
     */
    @Override
    public E delete(ID id){
        E e = super.delete(id);
        PrintWriter pw = null;
        try{
            pw = new PrintWriter(fileName);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        assert pw != null;
        pw.close();
        this.findAll().forEach(this::writeToFile);
        return e;
    }

    /**
     * Method which overrides the Repository interface update()
     * updates an entity stored in the repository and updates the file
     * @param entity to be updated
     * @return null if the entity was update, the entity otherwise(e.g. it doesn't exist)
     * @throws RepositoryException if the given entity is null
     * @throws FileNotFoundException if the file does not exist
     */
    @Override
    public E update(E entity){
        E e = super.update(entity);

        PrintWriter pw = null;
        try{
            pw = new PrintWriter(fileName);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        assert pw != null;
        pw.close();
        this.findAll().forEach(this::writeToFile);
        return e;
    }

}
