package com.projectmap2.Repository.DbRepos;

import com.projectmap2.Domain.Utilizator;
import com.projectmap2.Domain.Validators.UtilizatorValidator;
import com.projectmap2.Domain.Validators.Validator;
import com.projectmap2.Repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UtilizatorDbRepo implements Repository<Long,Utilizator> {
    private String url;
    private String username;
    private String password;
    private Validator<Utilizator> validator;

    public UtilizatorDbRepo(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = new UtilizatorValidator();
    }

    @Override
    public Optional<Utilizator> findOne(Long id) {
        Utilizator user;
        try(Connection connection = DriverManager.getConnection(url,username,password);
            ResultSet resultSet = connection.createStatement().executeQuery(String.format("select * from utilizator U where U.userId = '%d'",id))){
            if (resultSet.next()) {
                user = createUserFromResultSet(resultSet);
                return Optional.ofNullable(user);
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Utilizator createUserFromResultSet(ResultSet resultSet) {
        try {
            String firstName = resultSet.getString("Nume");
            String lastName = resultSet.getString("Prenume");
            String userName = resultSet.getString("Username");
            String parola = resultSet.getString("Parola");

            Long idd = resultSet.getLong("userId");
            Utilizator user = new Utilizator(firstName, lastName, userName, parola);
            user.setId(idd);
            return user;
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public Iterable<Utilizator> findAll() {
        Set<Utilizator> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from utilizator");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("userId");
                String firstName = resultSet.getString("Nume");
                String lastName = resultSet.getString("Prenume");
                String userName = resultSet.getString("Username");
                String parola = resultSet.getString("Parola");

                Utilizator utilizator = new Utilizator(firstName, lastName, userName, parola);
                utilizator.setId(id);
                users.add(utilizator);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public Optional<Utilizator> save(Utilizator entity) {
        String sql = "insert into utilizator (Nume, Prenume, Username, Parola) values (?, ?, ?, ?)";
        validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setString(3, entity.getUserName());
            ps.setString(4, entity.getParola());

            ps.executeUpdate();
        } catch (SQLException e) {
            return Optional.ofNullable(entity);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Utilizator> delete(Long id) {
        String sql = "delete from utilizator where userId = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            Optional<Utilizator> user = findOne(id);
            if(user.isPresent()) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Utilizator> update(Utilizator user) {
        if(user == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(user);
        String sql = "update Utilizator set Nume = ?, Prenume = ? where userId = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setLong(3, user.getId());
            if( ps.executeUpdate() > 0 )
                return Optional.empty();
            return Optional.ofNullable(user);
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

}