package ru.yandex.practicum.filmorate.storage.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.validationException.BadRequest;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaModel;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;
    private final LikeDbStorage likeDbStorage;
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public FilmDbStorage(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        genreDbStorage = new GenreDbStorage(jdbcTemplate);
        likeDbStorage = new LikeDbStorage(jdbcTemplate);
        mpaDbStorage = new MpaDbStorage(jdbcTemplate);
    }

    @Override
    public List<Film> findAll() {
        String filmRows = "select * from Film;";
        List<Film> films = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(filmRows);
        for (Map<String, Object> map : rows) {
            Film obj = new Film();
            obj.setId((Integer) map.get("id"));
            obj.setName((String) map.get("name"));
            obj.setDescription((String) map.get("description"));
            obj.setReleaseDate(Date.valueOf(map.get("release_date").toString()).toLocalDate());
            obj.setDuration((Integer) map.get("duration"));
            obj.setMpa(mpaDbStorage.getMpaModel((Integer) map.get("rating_id")));
            obj.setGenres(genreDbStorage.getGenresFilm(obj.getId()));
            obj.setLikes(likeDbStorage.getLikes(obj.getId()));
            films.add(obj);
        }
        return films;
    }

    @Override
    public Film create(Film film) {
        MpaModel rating = film.getMpa();
        Integer ratingId;
        if (rating == null) {
            ratingId = null;
        } else {
            ratingId = rating.getId();
        }

        String sql = "INSERT INTO Film (name, description, release_date, duration, rating_id)" +
                "VALUES(?,?,CAST(? AS date),?,?);";

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setObject(5, ratingId);
            return ps;
        }, keyHolder);

        int key = (int) keyHolder.getKey();
        film.setId(key);
        likeDbStorage.updateLikes(film.getLikes(), film.getId());
        genreDbStorage.updateGenre(film.getGenres(), film.getId());
        film.setMpa(mpaDbStorage.getMpaModel(film.getMpa().getId()));
        film.setGenres(genreDbStorage.getGenresFilm(film.getId()));
        return film;
    }

    @Override
    public Film update(Film film) {
        try {
            findFilmById(film.getId());
        } catch (InvalidIdException e) {
            throw new BadRequest("такого фильма нет в базе данных");
        }
        Integer ratingId = film.getMpa().getId();
        jdbcTemplate.update("UPDATE Film SET name=? WHERE id =?;", film.getName(), film.getId());
        jdbcTemplate.update("UPDATE Film SET description=? WHERE id =?;", film.getDescription(), film.getId());
        jdbcTemplate.update("UPDATE Film SET release_date=CAST(? AS date) WHERE id =?;", film.getReleaseDate().toString(), film.getId());
        jdbcTemplate.update("UPDATE Film SET duration=? WHERE id =?;", film.getDuration(), film.getId());
        jdbcTemplate.update("UPDATE Film SET rating_id=? WHERE id =?;", ratingId, film.getId());
        likeDbStorage.updateLikes(film.getLikes(), film.getId());
        film.setGenres(genreDbStorage.updateGenre(film.getGenres(), film.getId()));
        film.setMpa(mpaDbStorage.getMpaModel(film.getMpa().getId()));
        return film;
    }

    @Override
    public Film findFilmById(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from Film where id = ?;", id);
        if (filmRows.next()) {
            Film film = makeFilm(filmRows);
            film.setGenres(genreDbStorage.getGenresFilm(id));
            film.setLikes(likeDbStorage.getLikes(id));
            return film;
        } else {
            throw new BadRequest("фильма с таким id нет");
        }
    }

    private Film makeFilm(SqlRowSet filmRows) {
        Film film = new Film();
        film.setId(filmRows.getInt("id"));
        film.setName(filmRows.getString("name"));
        film.setDescription(filmRows.getString("description"));
        film.setReleaseDate(filmRows.getDate("release_date").toLocalDate());
        film.setDuration(filmRows.getInt("duration"));
        film.setMpa(mpaDbStorage.getMpaModel(filmRows.getInt("rating_id")));
        return film;
    }

    @Override
    public Film deleteById(Integer id) {
        Film film = findFilmById(id);
        if (!jdbcTemplate.queryForList("select id from FILM" +
                " where id = ?;", id).isEmpty()) {
            jdbcTemplate.update("DELETE From Film WHERE id = ?", id);
        }
        // cascade delete join-table
        return film;
    }
}
