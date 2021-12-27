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

        try (Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement("SELECT * from users WHERE id =?")) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next())
                throw new RepositoryException("Element doesn't exist!");
            Utilizator toReturn = new Utilizator(resultSet.getString("first_name"), resultSet.getString("last_name"),
                    "", "");
            toReturn.setId(resultSet.getLong("id"));
            return toReturn;
        } catch (SQLException e) {
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

                Utilizator utilizator = new Utilizator(firstName, lastName, "", "");
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

        String insert_user_sql = "insert into users ( first_name, last_name ) values (?,?)";
        String insert_credentials_sql = "insert into user_credentials (user_id, email, password) values (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement ps = connection.prepareStatement(insert_user_sql, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement ps_credentials = connection.prepareStatement(insert_credentials_sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());

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

        String updateUserSql = "UPDATE users" + " SET first_name =?, last_name =?" + " WHERE id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
                var statement = connection.prepareStatement(updateUserSql)) {
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setLong(3, entity.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException(e.getMessage());
        }
        return null;

    }

    private boolean checkIfEmailExists(String email) {
        String sql = "SELECT * FROM user_credentials WHERE email=?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
                var statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RepositoryException(e.getMessage());
        }

    }
}
