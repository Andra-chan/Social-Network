package socialnetwork.repository.database;

import javafx.util.Pair;
import socialnetwork.domain.Event;
import socialnetwork.domain.EventAttendance;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class EventAttendanceDbRepository implements Repository<Pair<Long, Long>, EventAttendance> {
    private final String url;
    private final String username;
    private final String password;

    public EventAttendanceDbRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public EventAttendance findOne(Pair<Long, Long> id) {
        if(id==null){
            throw new RepositoryException("id can't be null");
        }
        String sql = "select u.first_name as first_name, u.last_name as last_name," +
                "u.image_path as user_image_path,uc.email as email, u.id as user_id, e.title as title, " +
                "e.description as description, e.image_path as event_image_path, e.date as date, " +
                "e.id as event_id, e_u.notifications as notifications from events e " +
                "inner join events_users e_u on e_u.event_id=e.id " +
                "inner join users u on u.id=e_u.user_id " +
                "inner join user_credentials uc on uc.user_id=u.id " +
                "where event_id=? and user_id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id.getKey());
            statement.setLong(2, id.getValue());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                Utilizator user = new Utilizator(
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("email"),
                        ""
                );
                user.setImagePath(resultSet.getString("user_image_path"));
                user.setId(resultSet.getLong("user_id"));
                Event event = new Event(
                        resultSet.getString("event_image_path"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getTimestamp("date").toLocalDateTime()
                );
                event.setId(resultSet.getLong("event_id"));
                EventAttendance attendance = new EventAttendance(user, event);
                attendance.setId(new Pair<>(event.getId(), user.getId()));
                return attendance;
            }
            return null;
        }catch(Exception e){
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public Iterable<EventAttendance> findAll() {
        String sql = "select u.first_name as first_name, u.last_name as last_name," +
                "u.image_path as user_image_path,uc.email as email, u.id as user_id, e.title as title, " +
                "e.description as description, e.image_path as event_image_path, e.date as date, " +
                "e.id as event_id, e_u.notifications as notifications from events e " +
                "inner join events_users e_u on e_u.event_id=e.id " +
                "inner join users u on u.id=e_u.user_id " +
                "inner join user_credentials uc on uc.user_id=u.id";
        ArrayList<EventAttendance> attendances = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while(resultSet.next()){
                Utilizator user = new Utilizator(
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("email"),
                        ""
                );
                user.setImagePath(resultSet.getString("user_image_path"));
                user.setId(resultSet.getLong("user_id"));
                Event event = new Event(
                        resultSet.getString("event_image_path"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getTimestamp("date").toLocalDateTime()
                );
                event.setId(resultSet.getLong("event_id"));
                EventAttendance attendance = new EventAttendance(user, event);
                attendance.setNotifications(resultSet.getBoolean("notifications"));
                attendance.setId(new Pair<>(event.getId(), user.getId()));
                attendances.add(attendance);
            }
            return attendances;
        }catch(Exception e){
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public EventAttendance save(EventAttendance entity) {
        if(entity==null){
            throw new RepositoryException("Entity must not be null!");
        }
        String sql = "insert into events_users (event_id, user_id, notifications) values (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, entity.getId().getKey());
            ps.setLong(2, entity.getId().getValue());
            ps.setBoolean(3, entity.hasNotifications());
            ps.executeUpdate();
        }catch(Exception e){
            throw new RepositoryException(e.getMessage());
        }
        return null;
    }

    @Override
    public EventAttendance delete(Pair<Long, Long> id) {
        if(id==null){
            throw new RepositoryException("id can't be null");
        }
        String sql = "delete from events_users where event_id=? and user_id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id.getKey());
            ps.setLong(2, id.getValue());

            ps.executeUpdate();
        }catch(Exception e){
            throw new RepositoryException(e.getMessage());
        }
        return null;
    }

    @Override
    public EventAttendance update(EventAttendance entity) {
        if (entity == null) {
            throw new RepositoryException("Entity must not be null!");
        }
        String sql = "update events_users set notifications=? where entity_id=? and user_id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBoolean(1, entity.hasNotifications());
            ps.setLong(2, entity.getId().getKey());
            ps.setLong(3, entity.getId().getValue());
            ps.executeUpdate();

        }catch(Exception e){
            throw new RepositoryException(e.getMessage());
        }
        return null;
    }
}
