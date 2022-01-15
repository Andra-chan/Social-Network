package socialnetwork.repository.database;

import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class UtilizatorDbRepository implements Repository<Long, Utilizator> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Utilizator> validator;

    public UtilizatorDbRepository(String url, String username, String password, Validator<Utilizator> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Utilizator findOne(Long id) {

        String sql ="SELECT u.id as id, u.first_name as first_name, u.last_name as last_name, u.image_path as image_path, uc.email as email from users u inner join user_credentials uc on u.id=uc.user_id WHERE id =?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next())
                throw new RepositoryException("Element doesn't exist!");

            Utilizator toReturn = new Utilizator(resultSet.getString("first_name"), resultSet.getString("last_name"),
                    resultSet.getString("email"), "");
            toReturn.setImagePath(resultSet.getString("image_path"));
            toReturn.setId(resultSet.getLong("id"));
            return toReturn;
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public Iterable<Utilizator> findAll() {
        Set<Utilizator> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String imagePath = resultSet.getString("image_path");

                Utilizator utilizator = new Utilizator(firstName, lastName, "", "");
                utilizator.setImagePath(imagePath);
                utilizator.setId(id);
                users.add(utilizator);
            }
            return users;
        } catch (SQLException e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public Utilizator save(Utilizator entity) {
        if (entity == null)
            throw new RepositoryException("Entity must not be null!");
        validator.validate(entity);

        if (checkIfEmailExists(entity.getEmail())) {
            throw new RepositoryException("Email already exists");
        }

        String insert_user_sql = "insert into users ( first_name, last_name, image_path ) values (?,?, ?)";
        String insert_credentials_sql = "insert into user_credentials (user_id, email, password) values (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(insert_user_sql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement ps_credentials = connection.prepareStatement(insert_credentials_sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setString(3, entity.getImagePath());

            ps.executeUpdate();

            ResultSet generatedKeyRS = ps.getGeneratedKeys();
            generatedKeyRS.next();
            Long generatedKey = generatedKeyRS.getLong(1);

            ps_credentials.setLong(1, generatedKey);
            ps_credentials.setString(2, entity.getEmail());
            ps_credentials.setBytes(3, entity.getPassword().getBytes());
            ps_credentials.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException(e.getMessage());
        }
        return null;
    }

    @Override
    public Utilizator delete(Long id) {
        if (id == null)
            throw new RepositoryException("ID must not be null!");

        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException(e.getMessage());
        }
        return null;
    }

    @Override
    public Utilizator update(Utilizator entity) {
        if (entity == null)
            throw new RepositoryException("Entity must not be null!");
        validator.validate(entity);

        String updateUserSql = "UPDATE users" + " SET first_name =?, last_name =?, image_path=?" + " WHERE id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             var statement = connection.prepareStatement(updateUserSql)) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setString(3, entity.getImagePath());
            statement.setLong(4, entity.getId());
            if(statement.executeUpdate()!=0){
                return null;
            }else{
                return entity;
            }
        } catch (Exception e) {
            e.printStackTrace();
            //throw new RepositoryException(e.getMessage());
        }
        return entity;
    }

    private boolean checkIfEmailExists(String email) {
        String sql = "SELECT * FROM user_credentials WHERE email=?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             var statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet result = statement.executeQuery();
            return result.next();
        } catch (SQLException e) {
            throw new RepositoryException(e.getMessage());
        }
    }
}
