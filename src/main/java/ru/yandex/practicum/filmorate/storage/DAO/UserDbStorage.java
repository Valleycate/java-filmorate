package ru.yandex.practicum.filmorate.storage.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.validationException.BadRequest;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidIdException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.util.*;


@Repository
@Component
@Primary
public class UserDbStorage implements UserStorage {
    private static Integer idUser = 0;
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAll() {
        String userRows = "select * from Users;";
        List<User> users = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(userRows);

        for (Map row : rows) {
            User obj = new User();

            obj.setId((Integer) row.get("id"));
            obj.setName((String) row.get("name"));
            obj.setLogin((String) row.get("login"));
            obj.setEmail((String) row.get("email"));
            obj.setBirthday(Date.valueOf(row.get("birthday").toString()).toLocalDate());
            List<Map<String, Object>> friendshipRows = jdbcTemplate.queryForList("select friend_id, name from Friendship" +
                    " where user_id = ?;", obj.getId());
            Map<Integer, Friendship> friendship = new HashMap<>();
            Set<Integer> friends = new HashSet<>();
            for (Map map : friendshipRows) {
                friendship.put((Integer) map.get("friend_id"), toFriendship((String) map.get("name")));
                if (toFriendship((String) map.get("name")) == Friendship.CONFIRMED) {
                    friends.add((Integer) map.get("friend_id"));
                }
            }
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
        if (user.getFriendship() != null) {
            for (Integer friendId : user.getFriendship().keySet()) {
                jdbcTemplate.update("INSERT INTO Friendship (user_id,friend_id,name) VALUES(?,?,?);",
                        user.getId(), friendId, user.getFriendship().get(friendId).toString());
            }
        }
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
        if (user.getFriendship() != null) {
            for (Integer friendId : user.getFriendship().keySet()) {
                if (jdbcTemplate.queryForList("select friend_id from Friendship" +
                        " where user_id = ?;", user.getId()).isEmpty()) {
                    jdbcTemplate.update("INSERT INTO Friendship (user_id,friend_id,name) VALUES(?,?,?);",
                            user.getId(), friendId, user.getFriendship().get(friendId).toString());
                } else {
                    if (jdbcTemplate.queryForList("select friend_id from Friendship" +
                            " where friend_id = ? AND user_id = ?;", friendId, user.getId()).isEmpty()) {
                        jdbcTemplate.update("INSERT INTO Friendship (user_id,friend_id,name) VALUES(?,?,?);",
                                user.getId(), friendId, user.getFriendship().get(friendId).toString());
                    } else {
                        jdbcTemplate.update("UPDATE Friendship SET name=? WHERE friend_id = ? AND user_id =?;",
                                user.getFriendship().get(friendId).toString(), friendId, user.getId());
                    }
                }
            }
        }
        return user;

    }

    public void deleteFriend(Integer id, Integer friendId) {
        try {
            findUserById(id);
            findUserById(friendId);
        } catch (InvalidIdException e) {
            throw new BadRequest("такого пользователя нет в базе данных");
        }
        if (!jdbcTemplate.queryForList("select friend_id from Friendship" +
                " where user_id = ?;", id).isEmpty()) {
            jdbcTemplate.update("DELETE From FRIENDSHIP WHERE user_id = ? And friend_id = ?", id, friendId);
        }
    }

    public User findUserById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from Users where id = ?;", id);
        List<Map<String, Object>> friendshipRows = jdbcTemplate.queryForList("select user_id, friend_id, name from Friendship" +
                " where user_id = ?;", id);
        if (userRows.next()) {
            User user = makeUser(userRows);
            Map<Integer, Friendship> friendship = new HashMap<>();
            Set<Integer> friends = new HashSet<>();
            for (Map row : friendshipRows) {
                friendship.put((Integer) row.get("friend_id"), toFriendship((String) row.get("name")));
                friends.add((Integer) row.get("friend_id"));
            }
            user.setFriends(friends);
            user.setFriendship(friendship);
            return user;
        } else {
            throw new BadRequest("нет пользователя с таким id");
        }
    }

    private Friendship toFriendship(String name) {
        if (name != null) {
            if (name.contains("UNCONFIRMED") || name.contains("unconfirmed")) {
                return Friendship.UNCONFIRMED;
            } else if (name.contains("CONFIRMED") || name.contains("confirmed")) {
                return Friendship.CONFIRMED;
            }
        }
        return null;
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
