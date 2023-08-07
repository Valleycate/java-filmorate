package ru.yandex.practicum.filmorate.storage.DAO.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.validationException.BadRequest;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaModel;
import ru.yandex.practicum.filmorate.storage.DAO.Interface.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;
    private final LikeDbStorage likeDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final FilmDirectorsDbStorage filmDirectorsDbStorage;

    @Override
    public List<Film> findAll() {
        String filmRows = "select * from Film;";
        List<Film> films = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(filmRows);
        getFilmsList(films, rows);
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
        filmDirectorsDbStorage.updateDirectorsOfFilm(film.getId(), film.getDirectors());
        film.setMpa(mpaDbStorage.getMpaModel(film.getMpa().getId()));
        film.setGenres(new ArrayList<>(genreDbStorage.getGenresFilm(film.getId())));
        film.setDirectors(filmDirectorsDbStorage.getDirectorsOfFilm(film.getId()));
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
        filmDirectorsDbStorage.updateDirectorsOfFilm(film.getId(), film.getDirectors());
        film.setDirectors(filmDirectorsDbStorage.getDirectorsOfFilm(film.getId()));
        film.setGenres(new ArrayList<>(genreDbStorage.updateGenre(film.getGenres(), film.getId())));
        film.setMpa(mpaDbStorage.getMpaModel(film.getMpa().getId()));
        return film;
    }

    @Override
    public Film findFilmById(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from Film where id = ?;", id);
        if (filmRows.next()) {
            Film film = makeFilm(filmRows);
            film.setGenres(new ArrayList<>(genreDbStorage.getGenresFilm(id)));
            film.setLikes(likeDbStorage.getLikes(id));
            film.setDirectors(filmDirectorsDbStorage.getDirectorsOfFilm(film.getId()));
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
        film.setReleaseDate(Objects.requireNonNull(filmRows.getDate("release_date")).toLocalDate());
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

    public List<Film> findMutualFilms(int userId, int friendId) {
        List<Film> films = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT  *\n" +
                "FROM (SELECT  *\n" +
                "\tFROM FILM \n" +
                "\tINNER JOIN LIKES ON FILM.id = LIKES.film_id\n" +
                "\tWHERE LIKES.user_id = ? ) AS t\n" +
                "INNER JOIN LIKES ON t.id = LIKES.film_id\n" +
                "WHERE LIKES.user_id = ?;", userId, friendId);
        getFilmsList(films, rows);

        return films;
    }

    @Override
    public List<Film> getDirectorsFilms(int directorId) {
        String filmRows = "select * from Film " +
                "WHERE ID IN (" +
                "SELECT FILM_ID " +
                "FROM FILM_DIRECTORS " +
                "WHERE DIRECTOR_ID = ?)";

        List<Film> films = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(filmRows, directorId);
        getFilmsList(films, rows);
        return films;
    }

    @Override
    public List<Film> getSortedByYearFilmsOfDirector(int directorId) {
        String filmRows = "select * from Film " +
                "WHERE ID IN (" +
                "SELECT FILM_ID " +
                "FROM FILM_DIRECTORS " +
                "WHERE DIRECTOR_ID = ?) " +
                "ORDER BY RELEASE_DATE";

        List<Film> films = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(filmRows, directorId);
        getFilmsList(films, rows);
        return films;
    }

    @Override
    public List<Film> findTop10Films(int count, Integer genreId, Integer year) {
        String sqlString, sqlString1;
        Object[] localVar;
        sqlString1 = "SELECT F.*,  M.ID AS rating_id, POP.COUNT1 FROM FILM AS F " +
                "LEFT JOIN  (SELECT FILM_ID, COUNT(USER_ID) AS COUNT1 " +
                "FROM LIKES GROUP BY FILM_ID ) AS POP ON F.ID = POP.FILM_ID " +
                "LEFT JOIN MPA M on M.ID = F.RATING_ID ";

        if (genreId == null && year == null) {
            localVar = new Object[]{count};
            sqlString = sqlString1 + " ORDER BY POP.COUNT1 DESC LIMIT ?";
        } else if (genreId == null) {
            localVar = new Object[]{year, count};
            sqlString = sqlString1 + " WHERE  YEAR(F.RELEASE_DATE)   = ?" +
                    "        ORDER BY POP.COUNT1 DESC LIMIT ?";
        } else if (year == null) {
            localVar = new Object[]{genreId, count};
            sqlString = sqlString1 + " INNER JOIN FILM_GENRE FG on F.ID = FG.FILM_ID" +
                    "        INNER JOIN GENRE G1 on FG.GENRE_ID = G1.id" +
                    "        WHERE G1.id = ?" +
                    "        ORDER BY POP.COUNT1 DESC LIMIT ?";
        } else {
            localVar = new Object[]{genreId, year, count};
            sqlString = sqlString1 + "INNER JOIN FILM_GENRE FG on F.ID = FG.FILM_ID" +
                    "        INNER JOIN GENRE G1 on FG.GENRE_ID = G1.id" +
                    "        WHERE G1.id = ? AND YEAR(F.RELEASE_DATE)   = ?" +
                    "        ORDER BY POP.COUNT1 DESC LIMIT ?";
        }

        List<Film> films = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlString, localVar);
        getFilmsList(films, rows);
        return films;
    }

    @Override
    public List<Film> searchFilms(String query, List<String> searchByParams) {
        List<Film> films = new ArrayList<>();
        if (searchByParams.contains("title") && searchByParams.contains("director")) {
            films = searchFilmsByTitleAndDirector(query);
        } else if (searchByParams.contains("title") && !searchByParams.contains("director")) {
            films = searchFilmsByTitle(query);
        } else if (!searchByParams.contains("title") && searchByParams.contains("director")) {
            films = searchFilmsByDirector(query);
        }
        return films;
    }

    private List<Film> searchFilmsByDirector(String query) {
        String sqlQuery = "select f.* from Film as f " +
                "left join Film_Directors as fd on f.id = fd.film_id " +
                "left join Directors as d on fd.director_id = d.id " +
                "left join Likes as fl on f.id = fl.film_id " +
                "where d.name ilike ? " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(fl.USER_ID) desc";
        List<Film> films = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlQuery, "%" + query + "%");
        getFilmsList(films, rows);
        return films;
    }

    private List<Film> searchFilmsByTitle(String query) {
        String sqlQuery = "select f.* from Film as f " +
                "left join Likes as fl on f.id = fl.film_id " +
                "where f.name ilike ? " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(fl.USER_ID) desc";
        List<Film> films = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlQuery, "%" + query + "%");
        getFilmsList(films, rows);
        return films;
    }

    private List<Film> searchFilmsByTitleAndDirector(String query) {
        String sqlQuery = "select f.* from Film as f " +
                "left join Film_Directors as fd on f.id = fd.film_id " +
                "left join Directors as d on fd.director_id = d.id " +
                "left join Likes as fl on f.id = fl.film_id " +
                "where f.name ilike ? or d.name ilike ? " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(fl.USER_ID) desc";
        List<Film> films = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlQuery, "%" + query + "%", "%" + query + "%");
        getFilmsList(films, rows);
        return films;
    }

    private void getFilmsList(List<Film> films, List<Map<String, Object>> rows) {
        for (Map<String, Object> map : rows) {
            Film obj = new Film();
            obj.setId((Integer) map.get("id"));
            obj.setName((String) map.get("name"));
            obj.setDescription((String) map.get("description"));
            obj.setReleaseDate(Date.valueOf(map.get("release_date").toString()).toLocalDate());
            obj.setDuration((Integer) map.get("duration"));
            obj.setMpa(mpaDbStorage.getMpaModel((Integer) map.get("rating_id")));
            obj.setGenres(new ArrayList<>(genreDbStorage.getGenresFilm(obj.getId())));
            obj.setLikes(likeDbStorage.getLikes(obj.getId()));
            obj.setDirectors(filmDirectorsDbStorage.getDirectorsOfFilm(obj.getId()));
            films.add(obj);
        }
    }

    public List<Film> getRecommendations(int userId, int friendId) {
        List<Film> films = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT  *\n" +
                        "FROM FILM \n" +
                        "INNER JOIN LIKES ON FILM.id = LIKES.film_id\n" +
                        "WHERE LIKES.user_id = ? AND LIKES.user_id != ?;", friendId, userId);
        getFilmsList(films, rows);
        return films;
    }
}
