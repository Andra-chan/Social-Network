package socialnetwork.repository.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import socialnetwork.domain.UserCredentials;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

public class UserCredentialsDbRepository implements Repository<String, UserCredentials> {
    private final String url;
    private final String username;
    private final String password;

    public UserCredentialsDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public UserCredentials findOne(String email) {
        String sql = "select * from user_credentials where email=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet result = ps.executeQuery();
            if (!result.next()) {
                throw new RepositoryException("User doesn't exist");
            }

            UserCredentials credentials = new UserCredentials(result.getString("email"),
                    new String(result.getBytes("password")));
            credentials.setUserId(result.getLong("user_id"));
            return credentials;
        } catch (SQLException ex) {
            throw new RepositoryException(ex.getMessage());
        }
    }

    @Override
    public Iterable<UserCredentials> findAll() {
        return null;
    }

    @Override
    public UserCredentials save(UserCredentials entity) {
        return null;
    }

    @Override
    public UserCredentials delete(String id) {
        return null;
    }

    @Override
    public UserCredentials update(UserCredentials entity) {
        return null;
    }

}
