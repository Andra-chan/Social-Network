package socialnetwork.repository.database;

import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Prietenie;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class FriendRequestDbRepository implements Repository<Long, FriendRequest> {
    private String url;
    private String username;
    private String password;

    public FriendRequestDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public FriendRequest findOne(Long id) {
        Set<FriendRequest> friendRequests = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendrequests WHERE id =?")) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long sender = resultSet.getLong("sender");
                Long receiver = resultSet.getLong("receiver");
                String status = resultSet.getString("status");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

                FriendRequest friendRequest = new FriendRequest(sender, receiver);
                friendRequest.setId(id);
                friendRequest.setStatus(status);
                friendRequest.setLocalDateTime(date);
                return friendRequest;
            }
        } catch (SQLException e) {
            throw new RepositoryException("SQL exception!");
        }
        return null;
    }

    @Override
    public Iterable<FriendRequest> findAll() {
        Set<FriendRequest> friendRequests = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendrequests");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long sender = resultSet.getLong("sender");
                Long receiver = resultSet.getLong("receiver");
                String status = resultSet.getString("status");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

                FriendRequest friendRequest = new FriendRequest(sender, receiver);
                friendRequest.setId(id);
                friendRequest.setStatus(status);
                friendRequest.setLocalDateTime(date);
                friendRequests.add(friendRequest);
            }
            return friendRequests;
        } catch (SQLException e) {
            throw new RepositoryException("SQL exception!");
        }
    }

    @Override
    public FriendRequest save(FriendRequest entity) {
        if (entity == null)
            throw new RepositoryException("Entity must not be null!");

        FriendRequest friendRequest = (FriendRequest) entity;

        String queryFind = "select * from friendrequests where sender = (?) and receiver = (?) or sender= (?) and receiver = (?)";

        try (Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
             PreparedStatement ps = connection.prepareStatement(queryFind)) {

            ps.setLong(1, friendRequest.getSender());
            ps.setLong(2, friendRequest.getReceiver());
            ps.setLong(3, friendRequest.getSender());
            ps.setLong(4, friendRequest.getReceiver());

            ResultSet resultSet = ps.executeQuery();

            if (resultSet.next()) {
                return entity;
            }

        } catch (SQLException e) {
            throw new RepositoryException("SQL exception!");
        }

        String sql = "insert into friendrequests (sender, receiver, status, date) values (?,?,?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, friendRequest.getSender());
            ps.setLong(2, friendRequest.getReceiver());
            ps.setString(3, friendRequest.getStatus());
            ps.setTimestamp(4, Timestamp.valueOf(friendRequest.getLocalDateTime()));

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("SQL exception!");
        }
        return null;
    }

    @Override
    public FriendRequest delete(Long id) {
        if (id == null)
            throw new RepositoryException("ID must not be null!");

        String sql = "UPDATE friendrequests SET status ='REJECTED', date = ? WHERE id =?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("SQL exception!");
        }
        return null;
    }

    @Override
    public FriendRequest update(FriendRequest entity){
        if(entity == null)
            throw new RepositoryException("Entity must not be null!");

        String sql = "UPDATE friendrequests SET status = ?, date = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getStatus());
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(3, entity.getId());

            ps.executeUpdate();
        }catch (SQLException e){
            throw new RepositoryException("SQL exception!");
        }
        return null;
    }
}
