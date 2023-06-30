package ru.yandex.practicum.filmorate.storage.DAO;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class FriendsDbStorage {
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public Map<Integer, Friendship> findFriendship(int userId) {
        List<Map<String, Object>> friendshipRows = jdbcTemplate.queryForList("select friend_id, name from Friendship" +
                " where user_id = ?;", userId);
        Map<Integer, Friendship> friendship = new HashMap<>();
        for (Map<String, Object> map : friendshipRows) {
            friendship.put((Integer) map.get("friend_id"), toFriendship((String) map.get("name")));
        }
        return friendship;
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

    public void updateFriendship(Map<Integer, Friendship> friendship, int userId) {
        if (friendship != null) {
            for (Integer friendId : friendship.keySet()) {
                if (jdbcTemplate.queryForList("select friend_id from Friendship" +
                        " where user_id = ?;", userId).isEmpty()) {
                    jdbcTemplate.update("INSERT INTO Friendship (user_id,friend_id,name) VALUES(?,?,?);",
                            userId, friendId, friendship.get(friendId).toString());
                } else {
                    if (jdbcTemplate.queryForList("select friend_id from Friendship" +
                            " where friend_id = ? AND user_id = ?;", friendId, userId).isEmpty()) {
                        jdbcTemplate.update("INSERT INTO Friendship (user_id,friend_id,name) VALUES(?,?,?);",
                                userId, friendId, friendship.get(friendId).toString());
                    } else {
                        jdbcTemplate.update("UPDATE Friendship SET name=? WHERE friend_id = ? AND user_id =?;",
                                friendship.get(friendId).toString(), friendId, userId);
                    }
                }
            }
        }
    }

    public void deleteFriends(int id, int friendId) {
        if (!jdbcTemplate.queryForList("select friend_id from Friendship" +
                " where user_id = ?;", id).isEmpty()) {
            jdbcTemplate.update("DELETE From FRIENDSHIP WHERE user_id = ? And friend_id = ?", id, friendId);
        }
    }
}
