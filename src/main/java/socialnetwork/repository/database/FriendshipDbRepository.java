package socialnetwork.repository.database;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import javax.swing.text.html.parser.Entity;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FriendshipDbRepository implements Repository<Long, Prietenie> {
    private String url;
    private String username;
    private String password;


    public FriendshipDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Prietenie findOne(Long id) {
        Set<Prietenie> prietenii = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships WHERE id=?")) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long firstUser = resultSet.getLong("first_user");
                Long secondUser = resultSet.getLong("second_user");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

                Prietenie prietenie = new Prietenie(firstUser, secondUser, date);
                prietenie.setId(id);
                return prietenie;
            }
        } catch (SQLException e) {
            throw new RepositoryException("SQL exception!");
        }
        return null;
    }

    @Override
    public Iterable<Prietenie> findAll() {
        Set<Prietenie> prietenii = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long firstUser = resultSet.getLong("first_user");
                Long secondUser = resultSet.getLong("second_user");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

                Prietenie prietenie = new Prietenie(firstUser, secondUser, date);
                prietenie.setId(id);
                prietenii.add(prietenie);
            }
            return prietenii;
        } catch (SQLException e) {
            throw new RepositoryException("SQL exception!");
        }
    }

    @Override
    public Prietenie save(Prietenie entity) {
        if (entity == null)
            throw new RepositoryException("Entity must not be null!");

        Prietenie prietenie = (Prietenie) entity;

        String queryFind = "select * from friendships where first_user = (?) and second_user = (?) or first_user = (?) and second_user = (?)";

        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement ps = connection.prepareStatement(queryFind)) {

            ps.setLong(1, prietenie.getFirstUser());
            ps.setLong(2, prietenie.getSecondUser());
            ps.setLong(3, prietenie.getFirstUser());
            ps.setLong(4, prietenie.getSecondUser());

            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                return entity;
            }

        } catch (SQLException e) {
            throw new RepositoryException("SQL exception!");
        }

        String sql = "insert into friendships (id, first_user, second_user, date) values (?,?,?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getId());
            ps.setLong(2, entity.getFirstUser());
            ps.setLong(3, entity.getSecondUser());
            ps.setTimestamp(4, Timestamp.valueOf(entity.getDate()));

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("SQL exception!");
        }
        return null;
    }

    @Override
    public Prietenie delete(Long id) {
        if (id == null)
            throw new RepositoryException("ID must not be null!");

        String sql = "DELETE FROM friendships WHERE id = ?";
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
    public Prietenie update(Prietenie entity){
        return null;
    }
}