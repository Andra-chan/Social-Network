package socialnetwork.repository.database;


import socialnetwork.domain.Event;
import socialnetwork.domain.validators.EventValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import java.sql.*;
import java.util.ArrayList;

public class EventDbRepository implements Repository<Long, Event> {
    private final String url;
    private final String username;
    private final String password;
    private EventValidator validator;

    public EventDbRepository(String url, String username, String password, EventValidator validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator=validator;
    }

    @Override
    public Event findOne(Long id) {
        if(id==null){
            throw new RepositoryException("id can't be null");
        }
        String sql = "select * from events where id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                Event event =
                        new Event(resultSet.getString("image_path"),
                                resultSet.getString("title"),
                                resultSet.getString("description"),
                                resultSet.getTimestamp("date").toLocalDateTime());
                event.setId(resultSet.getLong("id"));
                return event;
            }
            return null;
        }
        catch(Exception e){
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public Iterable<Event> findAll() {
        ArrayList<Event> events = new ArrayList<>();
        String sql = "select * from events";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while(resultSet.next()){
                Event event =
                        new Event(resultSet.getString("image_path"),
                                resultSet.getString("title"),
                                resultSet.getString("description"),
                                resultSet.getTimestamp("date").toLocalDateTime());
                event.setId(resultSet.getLong("id"));
                events.add(event);
            }
            return events;
        } catch(Exception e){
            throw new RepositoryException(e.getMessage());
        }
    }

    @Override
    public Event save(Event entity) {
        if (entity == null)
            throw new RepositoryException("Entity must not be null!");
        validator.validate(entity);
        String sql = "insert into events (image_path, title, description, date) values (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getImagePath());
            ps.setString(2, entity.getTitle());
            ps.setString(3, entity.getDescription());
            ps.setTimestamp(4, Timestamp.valueOf(entity.getDate()));

            ps.executeUpdate();
        }catch(Exception e){
            throw new RepositoryException(e.getMessage());
        }
        return null;
    }

    @Override
    public Event delete(Long aLong) {
        return null;
    }

    @Override
    public Event update(Event entity) {
        return null;
    }
}