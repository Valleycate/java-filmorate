package ru.yandex.practicum.filmorate.storage.DAO.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DAO.Interface.UserStorage;
import ru.yandex.practicum.filmorate.storage.DAO.mapper.UserRowMapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final NamedParameterJdbcOperations jdbcOperations;
    private final UserRowMapper userRowMapper;

    public List<User> findAll() {
        final String sqlQuery = "select ID, NAME, LOGIN, EMAIL, BIRTHDAY from USERS ";
        return jdbcOperations.query(sqlQuery, userRowMapper);
    }

    public User create(User user) {
        String sqlQuery = "insert into USERS (EMAIL, LOGIN, NAME, BIRTHDAY)" +
                " values (:email, :login, :name, :birthday)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("email", user.getEmail());
        map.addValue("login", user.getLogin());
        map.addValue("name", user.getName());
        map.addValue("birthday", user.getBirthday());
        jdbcOperations.update(sqlQuery, map, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    public User update(User user) {
        String sqlQuery = "UPDATE USERS SET LOGIN = :login, NAME = :name, EMAIL = :email, BIRTHDAY = :birthday" +
                " WHERE ID = :userId";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("email", user.getEmail());
        map.addValue("login", user.getLogin());
        map.addValue("name", user.getName());
        map.addValue("birthday", user.getBirthday());
        map.addValue("userId", user.getId());
        jdbcOperations.update(sqlQuery, map);
        return user;
    }

    public void deleteFriend(Integer id, Integer friendId) {
        final String sqlQuery = "DELETE FROM FRIENDSHIP WHERE USER_ID = :userId AND FRIEND_Id = :frienId ";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("userId", id);
        map.addValue("frienId", friendId);
        jdbcOperations.update(sqlQuery, map);
    }

    public User findUserById(int id) {
        final String sqlQuery = "select ID, LOGIN, BIRTHDAY, EMAIL, NAME " +
                "from USERS " +
                "where ID = :userId ";
        final List<User> users = jdbcOperations.query(sqlQuery, Map.of("userId", id), userRowMapper);

        if (users.size() != 1) {
            return null; // Optional.empty();
        }
        return users.get(0);
    }

    @Override
    public User deleteById(Integer id) {
        User user = findUserById(id);
        String sqlQuery = "DELETE FROM USERS WHERE ID = :userId";
        jdbcOperations.update(sqlQuery, Map.of("userId", id));
        return user;
    }

    @Override
    public void addFriend(User user, User friend) {
        final String sqlQuery = "INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID) " +
                "VALUES (:userId, :frienId)";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("userId", user.getId());
        map.addValue("frienId", friend.getId());
        jdbcOperations.update(sqlQuery, map);
    }

    @Override
    public void deleteFriend(User user, User friend) {
        final String sqlQuery = "DELETE FROM FRIENDSHIP WHERE USER_ID = :userId AND FRIEND_Id = :frienId ";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("userId", user.getId());
        map.addValue("frienId", friend.getId());
        jdbcOperations.update(sqlQuery, map);
    }

    @Override
    public List<User> allFriends(User user) {
        final String sqlQuery = "SELECT ID, NAME, LOGIN, BIRTHDAY,EMAIL FROM USERS" +
                " WHERE ID IN (" +
                "SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID = :userId" +
                ")";
        return jdbcOperations.query(sqlQuery, Map.of("userId", user.getId()), userRowMapper);
    }

    @Override
    public List<User> getCommonFriends(int userId, int friendId) {
        final String sqlQuery = "SELECT ID, NAME, LOGIN, BIRTHDAY,EMAIL FROM USERS" +
                " WHERE ID IN (" +
                " SELECT F1.FRIEND_ID FROM FRIENDSHIP AS F1" +
                " INNER JOIN FRIENDSHIP AS F2 ON F1.FRIEND_ID = F2.FRIEND_ID" +
                "  WHERE F1.USER_ID = :USERID AND F2.USER_ID = :FRIENDID" +
                "    )";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("USERID", userId);
        map.addValue("FRIENDID", friendId);
        return jdbcOperations.query(sqlQuery, map, userRowMapper);
    }
}
