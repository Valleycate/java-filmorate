package ru.yandex.practicum.filmorate.storage.DAO.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NonexistentException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DAO.mapper.DirectorRowMapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class DirectorDbStorage {

    private final NamedParameterJdbcOperations jdbcOperations;

    public List<Director> getDirectors() {
        final String sqlQuery = "SELECT * FROM DIRECTORS";

        log.info("Get all directors.");
        return jdbcOperations.query(sqlQuery, new DirectorRowMapper());
    }

    public Director getDirector(int id) {
        final String sqlQuery = "SELECT * FROM DIRECTORS WHERE ID = :id ";
        final List<Director> directors = jdbcOperations.query(sqlQuery, Map.of("id", id), new DirectorRowMapper());

        if (directors.size() != 1) {
            log.info("Something wrong! Count of directors is {}", directors.size());
            throw new NonexistentException("Something wrong! Count of directors is " + directors.size());
        }

        log.info("Get director with id {}", id);
        return directors.get(0);
    }

    public Director add(Director director) {
        final String sqlQuery = "INSERT INTO DIRECTORS (NAME) " +
                "VALUES (:name) ";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("name", director.getName());
        jdbcOperations.update(sqlQuery, map, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        log.info("Put director with id {}", director.getId());
        return director;
    }

    public Director update(Director director) {

        if (getDirector(director.getId()) != null) {
            final String sqlQuery = "UPDATE DIRECTORS " +
                    "SET NAME = :name " +
                    "WHERE ID = :directorId";

            MapSqlParameterSource map = new MapSqlParameterSource();
            map.addValue("directorId", director.getId());
            map.addValue("name", director.getName());
            jdbcOperations.update(sqlQuery, map);

            log.info("Updated director with id {}", director.getId());
            return director;
        } else {
            throw new NonexistentException("Something wrong! There is no directors with id " + director.getId());
        }
    }

    public void delete(int id) {
        final String sqlQuery = "DELETE FROM DIRECTORS " +
                "WHERE ID = :id";

        jdbcOperations.update(sqlQuery, Map.of("id", id));
    }
}
