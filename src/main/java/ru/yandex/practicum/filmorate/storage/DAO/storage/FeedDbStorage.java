package ru.yandex.practicum.filmorate.storage.DAO.storage;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EnumEventType;
import ru.yandex.practicum.filmorate.model.enums.EnumOperation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository
public class FeedDbStorage {
    private final NamedParameterJdbcOperations jdbcOperations;

    public FeedDbStorage(NamedParameterJdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public List<Feed> findUsersFeed(Integer userId) {
        String sqlQuery = "select * from Feed where user_id = :user_id";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("user_id", userId);

        return jdbcOperations.query(sqlQuery, map, new FeedRowMapper());
    }

    public Feed save(Feed feed) {
        String sqlQuery = "insert into Feed (user_id, entity_id, event_type, operation, timestamp) " +
                "values (:user_id, :entity_id, :event_type, :operation, :timestamp)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("user_id", feed.getUserId());
        map.addValue("entity_id", feed.getEntityId());
        map.addValue("event_type", feed.getEventType().name());
        map.addValue("operation", feed.getOperation().name());
        map.addValue("timestamp", feed.getTimestamp());

        jdbcOperations.update(sqlQuery, map, keyHolder);
        feed.setEventId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return feed;
    }

    static class FeedRowMapper implements RowMapper<Feed> {
        @Override
        public Feed mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Feed(
                    rs.getLong("event_id"),
                    rs.getInt("user_id"),
                    rs.getLong("entity_id"),
                    EnumEventType.valueOf(rs.getString("event_type")),
                    EnumOperation.valueOf(rs.getString("operation")),
                    rs.getLong("timestamp")
            );
        }
    }
}
