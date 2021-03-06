package socialnetwork.repository.database;

import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

public class MessageDbRepository implements Repository<Long, Message> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Message> validator;

    public MessageDbRepository(String url, String username, String password, Validator<Message> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Message findOne(Long messageId) {
        if (messageId == null) {
            throw new RepositoryException("Entity id must not be null");
        }
        var messages = findAll();
        var message = StreamSupport.stream(messages.spliterator(), false)
                .filter(x -> x.getId().equals(messageId))
                .findAny();
        return message.get();
    }

    @Override
    public Iterable<Message> findAll() {
        String query = "SELECT * FROM messages";
        Map<Long, Message> messages = new HashMap<>();
        Map<Long, Long> parentIds = new HashMap<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Long id = result.getLong("id");
                Utilizator creator = getMessageCreator(id, connection);
                var to = getAllUsersForMessageId(id, connection);
                String message_body = result.getString("message_body");
                LocalDateTime date = result.getTimestamp("date").toLocalDateTime();
                Long parentId = result.getLong("parent_message_id");
                Message message = new Message(creator, to, message_body, date, null);
                message.setId(id);
                messages.put(id, message);
                parentIds.put(id, parentId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (var messageEntry : messages.entrySet()) {
            var messageId = messageEntry.getKey();
            if (parentIds.containsKey(messageId)) {
                Message parentMessage = messages.get(parentIds.get(messageId));
                messageEntry.getValue().setReply(parentMessage);
            }
        }
        return messages.values();
    }

    @Override
    public Message save(Message entity) {
        if (entity == null) {
            throw new RepositoryException("Entity must not be null");
        }
        validator.validate(entity);
        String insertMessageSql = "INSERT INTO messages (creator_id, message_body, date, parent_message_id) VALUES (?, ?, ?, ?)";
        String insertRelationSql = "INSERT INTO messages_recipients (message_id, recipient_id) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement insertMessageStatement = connection.prepareStatement(insertMessageSql,
                     Statement.RETURN_GENERATED_KEYS);
             PreparedStatement insertRelationStatement = connection.prepareStatement(insertRelationSql)) {

            insertMessageStatement.setLong(1, entity.getFrom().getId());
            insertMessageStatement.setString(2, entity.getMessageBody());
            insertMessageStatement.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            var reply = entity.getReply();
            if (reply != null) {
                insertMessageStatement.setLong(4, entity.getReply().getId());
            } else {
                insertMessageStatement.setNull(4, Types.NULL);
            }
            insertMessageStatement.executeUpdate();

            ResultSet generatedId = insertMessageStatement.getGeneratedKeys();
            generatedId.next();
            Long insertedMessageId = generatedId.getLong(1);

            for (var to : entity.getTo()) {
                insertRelationStatement.setLong(1, insertedMessageId);
                insertRelationStatement.setLong(2, to.getId());
                insertRelationStatement.executeUpdate();
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Message delete(Long id) {
        if (id == null) {
            throw new RepositoryException("ID must not be null!");
        }
        final String deleteSql = "DELETE FROM messages WHERE id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(deleteSql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Message update(Message entity) {
        return null;
    }

    private ArrayList<Utilizator> getAllUsersForMessageId(Long messageId, Connection connection) throws SQLException {
        final String query = "SELECT u.id as user_id, u.first_name as first_name, u.last_name as last_name FROM messages m INNER JOIN messages_recipients m_r ON m.id=m_r.message_id INNER JOIN users u ON u.id=m_r.recipient_id WHERE m.id=?;";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, messageId);
        ResultSet result = statement.executeQuery();
        ArrayList<Utilizator> users = new ArrayList<>();
        while (result.next()) {
            Utilizator user = getUserFromResultSet(result);
            users.add(user);
        }
        return users;
    }

    private Utilizator getMessageCreator(Long messageId, Connection connection) throws SQLException {
        final String query = "SELECT u.id as user_id, u.first_name as first_name, u.last_name as last_name FROM messages m INNER JOIN users u ON m.creator_id=u.id WHERE m.id=?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, messageId);
        ResultSet result = statement.executeQuery();
        if (result.next()) {
            return getUserFromResultSet(result);
        }
        return null;
    }

    private Utilizator getUserFromResultSet(ResultSet result) throws SQLException {
        Long id = result.getLong("user_id");
        String first_name = result.getString("first_name");
        String last_name = result.getString("last_name");
        Utilizator user = new Utilizator(first_name, last_name);
        user.setId(id);
        return user;
    }
}
