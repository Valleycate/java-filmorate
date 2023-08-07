package ru.yandex.practicum.filmorate.storage.DAO.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DAO.mapper.DirectorRowMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class FilmDirectorsDbStorage {

    private final NamedParameterJdbcOperations jdbcOperations;

    public void updateDirectorsOfFilm(int filmId, List<Director> directorsId) {

        if (directorsId != null) {
            for (Director i : directorsId) {
                final String sqlQuery = "DELETE FROM FILM_DIRECTORS " +
                        "WHERE FILM_ID = :filmId; " +
                        "INSERT INTO FILM_DIRECTORS (FILM_ID, DIRECTOR_ID) " +
                        "VALUES (:filmId, :directorId) ";

                MapSqlParameterSource map = new MapSqlParameterSource();
                map.addValue("directorId", i.getId());
                map.addValue("filmId", filmId);

                jdbcOperations.update(sqlQuery, map);

            }
            log.info("Directors in film with id {} updated", filmId);
        } else {
            final String sqlQuery = "DELETE FROM FILM_DIRECTORS " +
                    "WHERE FILM_ID = :filmId";

            jdbcOperations.update(sqlQuery, Map.of("filmId", filmId));
        }
    }

    public List<Director> getDirectorsOfFilm(int filmId) {

        final String sqlQuery = "SELECT * " +
                "FROM DIRECTORS " +
                "WHERE ID IN (SELECT DIRECTOR_ID " +
                "FROM FILM_DIRECTORS " +
                "WHERE FILM_ID = :filmId)";

        ArrayList<Director> list = new ArrayList<>(jdbcOperations.query(sqlQuery, Map.of("filmId", filmId), new DirectorRowMapper()));

        log.info("Get directors of film with id {}.", filmId);
        return Objects.requireNonNullElseGet(list, ArrayList::new);
    }
}
