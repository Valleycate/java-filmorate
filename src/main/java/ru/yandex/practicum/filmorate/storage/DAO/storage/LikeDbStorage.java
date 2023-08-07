package ru.yandex.practicum.filmorate.storage.DAO.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class LikeDbStorage {
    private final JdbcTemplate jdbcTemplate;

    protected void updateLikes(Set<Integer> likes, int filmId) {
        jdbcTemplate.update("DELETE FROM Likes WHERE film_id =?;", filmId);
        for (Integer id : likes) {
            jdbcTemplate.update("INSERT INTO Likes (film_id, user_id) VALUES(?,?);", filmId, id);
        }
    }

    protected Set<Integer> getLikes(int filmId) {
        List<Map<String, Object>> rowsLikes = jdbcTemplate.queryForList("select user_id from Likes where film_id = ?;", filmId);
        HashSet<Integer> filmLikes = new HashSet<>();
        for (Map<String, Object> row : rowsLikes) {
            filmLikes.add((Integer) row.get("user_id"));
        }
        return filmLikes;
    }
}
