package com.projectmap2.Repository.DbRepos;

import com.projectmap2.Domain.FriendshipStatus;
import com.projectmap2.Domain.Prietenie;
import com.projectmap2.Domain.Tuple;
import com.projectmap2.Domain.Utilizator;
import com.projectmap2.Domain.Validators.PrietenieValidator;
import com.projectmap2.Domain.Validators.Validator;
import com.projectmap2.Repository.PagedRepository;
import com.projectmap2.Repository.Repository;
import com.projectmap2.Utils.Paging.Page;
import com.projectmap2.Utils.Paging.Pageable;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PrietenieDbRepo implements PagedRepository<Tuple<Long,Long>, Prietenie> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Prietenie> validator;

    public PrietenieDbRepo(String url,String username,String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        validator = new PrietenieValidator();
    }


    @Override
    public Optional<Prietenie> findOne(Tuple<Long,Long> id)
    {
        Prietenie prietenie;
        try(Connection connection = DriverManager.getConnection(url,username,password);
            ResultSet resultSet = connection.createStatement().executeQuery(String.format("""
                            SELECT P.dataprietenie AS Data , P.status AS Status ,
                             U1.userid AS IdUser1, U1.nume AS NumeUser1, U1.prenume AS PrenumeUser1, U1.username AS UsernameUser1, U1.parola AS ParolaUser1,
                             U2.userid AS IdUser2, U2.nume AS NumeUser2, U2.prenume AS PrenumeUser2, U2.username AS UsernameUser2, U2.parola AS ParolaUser2
                            FROM Prietenie P
                            JOIN Utilizator U1 ON P.user1 = U1.userId
                            JOIN Utilizator U2 ON P.user2 = U2.userId
                            where P.User1 = '%d' and P.User2 = '%d'"""
                            ,id.getLeft(),id.getRight()))){
            if (resultSet.next()) {
                prietenie = createFriendshipFromResultSet(resultSet);
                return Optional.ofNullable(prietenie);
            }
        }catch(SQLException e)
        {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Prietenie createFriendshipFromResultSet(ResultSet resultSet)
    {
        try {
            Long user1Id  = resultSet.getLong("iduser1");
            String firstNameUser1 = resultSet.getString("numeUser1");
            String lastNameUser1 = resultSet.getString("prenumeUser1");
            String userNameUser1 = resultSet.getString("usernameUser1");
            String parolaUser1 = resultSet.getString("parolaUser1");

            Long user2Id  = resultSet.getLong("iduser2");
            String firstNameUser2 = resultSet.getString("numeUser2");
            String lastNameUser2 = resultSet.getString("prenumeUser2");
            String userNameUser2 = resultSet.getString("usernameUser2");
            String parolaUser2 = resultSet.getString("parolaUser2");

            Utilizator user1 = new Utilizator(firstNameUser1,lastNameUser1,userNameUser1,parolaUser1);
            Utilizator user2 = new Utilizator(firstNameUser2,lastNameUser2,userNameUser2,parolaUser2);
            user1.setId(user1Id);
            user2.setId(user2Id);

            FriendshipStatus status = FriendshipStatus.valueOf(resultSet.getString("status"));
            LocalDateTime date = resultSet.getTimestamp("data").toLocalDateTime();

            return new Prietenie(user1,user2,date,status);
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Iterable<Prietenie> findAll() {
        Set<Prietenie> friendShips = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT P.dataprietenie AS Data , P.status AS Status ,
                      U1.userid AS IdUser1, U1.nume AS NumeUser1, U1.prenume AS PrenumeUser1, U1.username AS UsernameUser1, U1.parola AS ParolaUser1,
                      U2.userid AS IdUser2, U2.nume AS NumeUser2, U2.prenume AS PrenumeUser2, U2.username AS UsernameUser2, U2.parola AS ParolaUser2
                     FROM Prietenie P
                     JOIN Utilizator U1 ON P.user1 = U1.userId
                     JOIN Utilizator U2 ON P.user2 = U2.userId""");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Prietenie friendShip = createFriendshipFromResultSet(resultSet);
                friendShips.add(friendShip);
            }
            return friendShips;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendShips;
    }


    @Override
    public Optional<Prietenie> save(Prietenie entity) {
        String sql = "insert into prietenie (User1,User2,DataPrietenie,Status) values (?, ?, ?, ?)";
        validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, entity.getId().getLeft());
            ps.setLong(2, entity.getId().getRight());
            ps.setTimestamp(3,Timestamp.valueOf(entity.getDate()));
            ps.setString(4,entity.getStatus().toString());

            ps.executeUpdate();
        } catch (SQLException e) {
            return Optional.ofNullable(entity);
        }
        return Optional.empty();
    }


    @Override
    public Optional<Prietenie> delete(Tuple<Long,Long> id) {
        String sql = "delete from prietenie where User1 = ? and User2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            Optional<Prietenie> prietenie = findOne(id);
            if(prietenie.isPresent()) {
                ps.setLong(1, id.getLeft());
                ps.setLong(2, id.getRight());
                ps.executeUpdate();
            }
            return prietenie;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Prietenie> update(Prietenie prietenie) { // maybe modified ?
        if(prietenie == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(prietenie);
        String sql = "update prietenie set DataPrietenie = ? , Status = ? where User1 = ? and User2 = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1,Timestamp.valueOf(prietenie.getDate()));
            ps.setString(2,prietenie.getStatus().toString());
            ps.setLong(3, prietenie.getId().getLeft());
            ps.setLong(4, prietenie.getId().getRight());

            if( ps.executeUpdate() > 0 )
                return Optional.empty();
            return Optional.ofNullable(prietenie);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public int findLength()
    {
        final int[] ret = {0};
        findAll().forEach(x-> ret[0] += 1);
        return ret[0];
    }

    @Override
    public Page<Prietenie> findAllOnPage(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        Set<Prietenie> friendShips = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT P.dataprietenie AS Data , P.status AS Status ,
                      U1.userid AS IdUser1, U1.nume AS NumeUser1, U1.prenume AS PrenumeUser1, U1.username AS UsernameUser1, U1.parola AS ParolaUser1,
                      U2.userid AS IdUser2, U2.nume AS NumeUser2, U2.prenume AS PrenumeUser2, U2.username AS UsernameUser2, U2.parola AS ParolaUser2
                     FROM Prietenie P
                     JOIN Utilizator U1 ON P.user1 = U1.userId
                     JOIN Utilizator U2 ON P.user2 = U2.userId
                     """ + "LIMIT " + pageSize + " OFFSET " + (pageSize * pageNumber + 1));
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Prietenie friendShip = createFriendshipFromResultSet(resultSet);
                friendShips.add(friendShip);
            }
            return new Page<>(friendShips, friendShips.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Page<>(friendShips, friendShips.size());
    }


    public Page<Prietenie> findAllOnPage(Pageable pageable,Long mainUserId) {
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        Set<Prietenie> friendShips = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("""
                     SELECT P.dataprietenie AS Data , P.status AS Status ,
                      U1.userid AS IdUser1, U1.nume AS NumeUser1, U1.prenume AS PrenumeUser1, U1.username AS UsernameUser1, U1.parola AS ParolaUser1,
                      U2.userid AS IdUser2, U2.nume AS NumeUser2, U2.prenume AS PrenumeUser2, U2.username AS UsernameUser2, U2.parola AS ParolaUser2
                     FROM Prietenie P
                     JOIN Utilizator U1 ON P.user1 = U1.userId
                     JOIN Utilizator U2 ON P.user2 = U2.userId""" +
                     " where (P.User1 = "+mainUserId+" OR P.User2 = "+mainUserId+ ") AND P.Status = 'ACCEPTED'  "+
                     "ORDER BY P.dataprietenie "+
                     " LIMIT " + pageSize + " OFFSET " + (pageSize * pageNumber));
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Prietenie friendShip = createFriendshipFromResultSet(resultSet);
                friendShips.add(friendShip);
            }
            return new Page<>(friendShips, friendShips.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Page<>(friendShips, friendShips.size());
    }
}
