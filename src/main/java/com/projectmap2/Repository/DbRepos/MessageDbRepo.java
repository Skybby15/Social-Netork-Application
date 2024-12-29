package com.projectmap2.Repository.DbRepos;

import com.projectmap2.Domain.Message;
import com.projectmap2.Repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MessageDbRepo implements Repository<Long,Message> {
    private final String url;
    private final String username;
    private final String password;

    public MessageDbRepo(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Message> findOne(Long messageId) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Mesaj M FULL JOIN Receiver R ON M.messageid = R.messageid where M.messageId = " + messageId);
             ResultSet resultSet = statement.executeQuery()) {

            Message resultMessage = null;

            while (resultSet.next()) {
                resultMessage = createAndAddMessageFromResultSet(resultSet,resultMessage,null);
            }

            return Optional.ofNullable(resultMessage);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Iterable<Message> findAll() {
        ArrayList<Message> messages = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM Mesaj M FULL JOIN Receiver R ON M.messageid = R.messageid ORDER BY M.messageid ");
             ResultSet resultSet = statement.executeQuery()) {

            Message lastMessage = null;

            while (resultSet.next()) {
                lastMessage = createAndAddMessageFromResultSet(resultSet,lastMessage,messages);
            }

            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }

    public List<Message> findAllPrivateMessages(Long user1, Long user2) {
        ArrayList<Message> messages = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(  "SELECT * FROM Mesaj M\n" +
                                                                             "FULL JOIN Receiver R\n" +
                                                                             "ON M.messageid = R.messageid\n" +
                                                                             "where M.sender = "+ user1 +" AND R.receiveruser = "+ user2 +" OR M.sender = "+ user2 +" AND R.receiveruser = "+ user1 +"\n" +
                                                                             "ORDER BY M.messageid ASC");
             ResultSet resultSet = statement.executeQuery()) {

            Message lastMessage = null;

            while (resultSet.next()) {
                lastMessage = createAndAddMessageFromResultSet(resultSet,lastMessage,messages);
            }

            return messages;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }

    private Message createAndAddMessageFromResultSet(ResultSet resultSet, Message lastMessage, List<Message> messages) throws SQLException {
        long messageId = resultSet.getLong("MessageId");
        long receiverId = resultSet.getLong("ReceiverUser");
        if(lastMessage != null && messageId == lastMessage.getId())
        {
            lastMessage.getReceivers().add(receiverId);
            return lastMessage;
        }else
        {
            Long senderId = resultSet.getLong("Sender");
            String messageText = resultSet.getString("Message");
            LocalDateTime date = resultSet.getTimestamp("Date").toLocalDateTime();

            long replyMessageId = resultSet.getLong("ReplyMessage");
            Optional<Message> replyMessage;
            if(replyMessageId != 0)
                replyMessage = findOne(replyMessageId);
            else
                replyMessage = Optional.empty();

            Message resultMessage;

            if(replyMessage.isPresent())
            {
                resultMessage = new Message(senderId,receiverId,messageText,replyMessage.get(),date);
            }
            else
                resultMessage = new Message(senderId, receiverId,messageText,date);
            resultMessage.setId(messageId);

            if(messages != null)
                messages.add(resultMessage);

            return resultMessage;
        }
    }

    @Override
    public Optional<Message> save(Message entity) {
        String sql = "insert into Mesaj (Sender, Message, Date, ReplyMessage) values (?, ?, ?, ?)";
        //TODO validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getSender());
            ps.setString(2, entity.getText());
            ps.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
            if(entity.getReplyMessage() != null)
                ps.setLong(4, entity.getReplyMessage().getId());
            else
                ps.setNull(4, Types.NULL);

            ps.executeUpdate();

            Connection connection2 = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection2.prepareStatement("SELECT * from Mesaj M where M.Date = ?");
            statement.setTimestamp(1, Timestamp.valueOf(entity.getDate()));
            ResultSet resultSet = statement.executeQuery();

            resultSet.next();
            long messageId = resultSet.getLong("MessageId");

            for (Long receiver : entity.getReceivers()) {
                sql = "insert into Receiver (ReceiverUser, MessageID) values (?, ?)";
                Connection connection3 = DriverManager.getConnection(url, username, password);
                PreparedStatement ps2 = connection3.prepareStatement(sql);

                    ps2.setLong(1, receiver);
                    ps2.setLong(2, messageId);

                    ps2.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.ofNullable(entity);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Message> delete(Long aLong) {
        return Optional.empty();
    }

    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }

    @Override
    public int findLength() {
        return 0;
    }
}
