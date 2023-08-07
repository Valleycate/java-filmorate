package ru.yandex.practicum.filmorate.storage.DAO.storage;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReviewLikeDbStorage {
    private static final int LIKE = 1;
    private static final int DISLIKE = -1;
    private final NamedParameterJdbcOperations jdbcOperations;

    public ReviewLikeDbStorage(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public void addLikeDislike(Long reviewId, int userId, boolean isLike) {
        String sqlQuery = "insert into Review_like (review_id, user_id, like_dislike) values (:review_id, :user_id, :like_dislike)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("review_id", reviewId);
        map.addValue("user_id", userId);
        if (isLike) {
            map.addValue("like_dislike", LIKE);
        } else {
            map.addValue("like_dislike", DISLIKE);
        }

        jdbcOperations.update(sqlQuery, map, keyHolder);
    }

    public void deleteLikeDislike(Long reviewId, int userId) {
        String sqlQuery = "delete from Review_like where user_id = :user_id and review_id = :review_id";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("review_id", reviewId);
        map.addValue("user_id", userId);

        jdbcOperations.update(sqlQuery, map, keyHolder);
    }

    public Integer sumLikeDislike(Long reviewId) {
        String sqlQuery = "select sum(like_dislike) as useful from Review_like where review_id = :review_id";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("review_id", reviewId);
        return jdbcOperations.queryForObject(sqlQuery, map, Integer.class);
    }

    public List<Integer> whoLikeReview(Long reviewId) {
        String sqlQuery = "select user_id from Review_like where review_id = :review_id and like_dislike = :LIKE";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("review_id", reviewId);
        map.addValue("LIKE", LIKE);
        return jdbcOperations.query(sqlQuery, map, (rs, rowNum) -> rs.getInt("user_id"));
    }

    public List<Integer> whoDislikeReview(Long reviewId) {
        String sqlQuery = "select user_id from Review_like where review_id = :review_id and like_dislike = :DISLIKE";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("review_id", reviewId);
        map.addValue("DISLIKE", DISLIKE);
        return jdbcOperations.query(sqlQuery, map, (rs, rowNum) -> rs.getInt("user_id"));
    }

}
