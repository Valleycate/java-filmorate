package ru.yandex.practicum.filmorate.storage.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.validationException.BadRequest;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidIdException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.sql.DataSource;
import java.sql.Date;
import java.util.*;


@Repository
@Primary
public class UserDbStorage implements UserStorage {
    private static Integer idUser = 0;
    private final JdbcTemplate jdbcTemplate;
    FriendsDbStorage friendsDbStorage;

    @Autowired
    public UserDbStorage(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        friendsDbStorage = new FriendsDbStorage(jdbcTemplate);
    }

    public List<User> findAll() {
        String userRows = "select * from Users;";
        List<User> users = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(userRows);
        for (Map<String, Object> row : rows) {
            User obj = new User();
            obj.setId((Integer) row.get("id"));
            obj.setName((String) row.get("name"));
            obj.setLogin((String) row.get("login"));
            obj.setEmail((String) row.get("email"));
            obj.setBirthday(Date.valueOf(row.get("birthday").toString()).toLocalDate());
            Map<Integer, Friendship> friendship = friendsDbStorage.findFriendship(obj.getId());
            Set<Integer> friends = new HashSet<>(friendship.keySet());
            obj.setFriends(friends);
            obj.setFriendship(friendship);
            users.add(obj);
        }
        if (!users.isEmpty()) {
            return users;
        } else {
            throw new BadRequest("нет пользователей");
        }
    }

    public User create(User user) {
        idUser = idUser + 1;
        user.setId(idUser);
        jdbcTemplate.update("INSERT INTO Users (id,name, email, login, birthday) VALUES(?,?,?,?,CAST(? AS date));",
                user.getId(), user.getName(), user.getEmail(), user.getLogin(), user.getBirthday());
        friendsDbStorage.updateFriendship(user.getFriendship(), user.getId());
        return user;
    }

    public User update(User user) {
        try {
            findUserById(user.getId());
        } catch (InvalidIdException e) {
            throw new BadRequest("такого пользователя нет в базе данных");
        }
        jdbcTemplate.update("UPDATE Users SET name=? WHERE id =?;", user.getName(), user.getId());
        jdbcTemplate.update("UPDATE Users SET email=? WHERE id =?;", user.getEmail(), user.getId());
        jdbcTemplate.update("UPDATE Users SET login=? WHERE id =?;", user.getLogin(), user.getId());
        jdbcTemplate.update("UPDATE Users SET birthday=CAST(? AS date) WHERE id =?;", user.getBirthday().toString(), user.getId());
        friendsDbStorage.updateFriendship(user.getFriendship(), user.getId());
        return user;

    }

    public void deleteFriend(Integer id, Integer friendId) {
        try {
            findUserById(id);
            findUserById(friendId);
        } catch (InvalidIdException e) {
            throw new BadRequest("такого пользователя нет в базе данных");
        }
        friendsDbStorage.deleteFriends(id, friendId);
    }

    public User findUserById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from Users where id = ?;", id);
        if (userRows.next()) {
            User user = makeUser(userRows);
            Map<Integer, Friendship> friendship = friendsDbStorage.findFriendship(id);
            ;
            Set<Integer> friends = new HashSet<>(friendship.keySet());
            user.setFriends(friends);
            user.setFriendship(friendship);
            return user;
        } else {
            throw new BadRequest("нет пользователя с таким id");
        }
    }

    private User makeUser(SqlRowSet userRows) {
        User user = new User();
        user.setId(userRows.getInt("id"));
        user.setName(userRows.getString("name"));
        user.setLogin(userRows.getString("login"));
        user.setEmail(userRows.getString("email"));
        user.setBirthday((userRows.getDate("birthday").toLocalDate()));
        return user;
    }


}
