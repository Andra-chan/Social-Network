package socialnetwork.repository.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

public class UtilizatorDbRepository implements Repository<Long, Utilizator> {
    private String url;
    private String username;
    private String password;
    private Validator<Utilizator> validator;

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
            Utilizator toReturn = new Utilizator(resultSet.getString("first_name"), resultSet.getString("last_name"));
            toReturn.setId(resultSet.getLong("id"));
            return toReturn;
        } catch (SQLException e) {
            throw new RepositoryException("SQL exception!");
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

                Utilizator utilizator = new Utilizator(firstName, lastName);
                utilizator.setId(id);
                users.add(utilizator);
            }
            return users;
        } catch (SQLException e) {
            throw new RepositoryException("SQL exception!");
        }
    }

    @Override
    public Utilizator save(Utilizator entity) {
        if (entity == null)
            throw new RepositoryException("Entity must not be null!");
        validator.validate(entity);

        String sql = "insert into users ( first_name, last_name ) values (?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("SQL exception!");
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
            throw new RepositoryException("SQL exception!");
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
            throw new RepositoryException("SQL exception!");
        }
        return null;

    }
}
