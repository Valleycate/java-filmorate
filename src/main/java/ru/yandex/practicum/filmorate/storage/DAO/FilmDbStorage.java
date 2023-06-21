package ru.yandex.practicum.filmorate.storage.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NonexistentException;
import ru.yandex.practicum.filmorate.exceptions.validationException.BadRequest;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.GenreModel;
import ru.yandex.practicum.filmorate.model.MpaModel;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    private Integer idFilm = 0;
    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> findAll() {
        String filmRows = "select * from Film;";
        List<Film> films = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(filmRows);

        for (Map map : rows) {
            Film obj = new Film();

            obj.setId((Integer) map.get("id"));
            obj.setName((String) map.get("name"));
            obj.setDescription((String) map.get("description"));
            obj.setReleaseDate(Date.valueOf(map.get("release_date").toString()).toLocalDate());
            obj.setDuration((Integer) map.get("duration"));
            obj.setMpa(IdtoRating((Integer) map.get("rating_id")));
            List<Map<String, Object>> rowsGenre = jdbcTemplate.queryForList("select genre_id " +
                    "from Film_genre " +
                    "where film_id = ?;", obj.getId());
            ArrayList<Integer> filmGenre = new ArrayList<>();
            for (Map row : rowsGenre) {
                filmGenre.add((Integer) row.get("genre_id"));
            }
            obj.setGenres(idToGenre(filmGenre));
            List<Map<String, Object>> rowsLikes = jdbcTemplate.queryForList("select user_id from Likes where film_id = ?;", obj.getId());
            ArrayList<Integer> filmLikes = new ArrayList<>();
            for (Map row : rowsLikes) {
                filmLikes.add((Integer) row.get("user_id"));
            }
            obj.setLikes(new HashSet<>(filmLikes));
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
        List<Integer> genreId = genreToId(film.getGenres());
        idFilm = idFilm + 1;
        film.setId(idFilm);
        jdbcTemplate.update("  INSERT INTO Film (id, name, description, release_date, duration, rating_id)" +
                "VALUES(?,?,?,CAST(? AS date),?,?);", film.getId(), film.getName(), film.getDescription(), film.getReleaseDate().toString(), film.getDuration(), ratingId);
        for (Integer id : film.getLikes()) {
            jdbcTemplate.update("INSERT INTO Likes (film_id, user_id) VALUES(?,?);", film.getId(), id);
        }
        if (genreId != null) {
            for (Integer id : genreId) {
                jdbcTemplate.update("INSERT INTO Film_genre (film_id, genre_id) VALUES(?,?);", film.getId(), id);
            }
        }
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
        List<Integer> genreId = genreToId(film.getGenres());
        jdbcTemplate.update("UPDATE Film SET name=? WHERE id =?;", film.getName(), film.getId());
        jdbcTemplate.update("UPDATE Film SET description=? WHERE id =?;", film.getDescription(), film.getId());
        jdbcTemplate.update("UPDATE Film SET release_date=CAST(? AS date) WHERE id =?;", film.getReleaseDate().toString(), film.getId());
        jdbcTemplate.update("UPDATE Film SET duration=? WHERE id =?;", film.getDuration(), film.getId());
        jdbcTemplate.update("UPDATE Film SET rating_id=? WHERE id =?;", ratingId, film.getId());
        for (Integer id : film.getLikes()) {
            jdbcTemplate.update("UPDATE Likes SET user_id=? WHERE film_id=?;", id, film.getId());
        }
        if (genreId != null) {
            List<Integer> iteration = new ArrayList<>();
            jdbcTemplate.update("DELETE FROM FILM_GENRE WHERE film_id =?;", film.getId());
            for (int i =0; i < genreId.size(); i++) {
                Integer id = genreId.get(i);
                if(!iteration.contains(id)) {
                    jdbcTemplate.update("INSERT INTO Film_genre (film_id, genre_id) VALUES(?,?);", film.getId(), id);
                    iteration.add(id);
                }else{
                    film.getGenres().remove(i);
                }
            }
        }
        return film;
    }

    public List<MpaModel> findAllMPA() {
        String sql = "select * From MPA";
        return jdbcTemplate.query(sql, this::makeMpa);
    }

    private MpaModel makeMpa(ResultSet result, int rowNum) throws SQLException {
        MpaModel model = new MpaModel();
        model.setId(result.getInt("id"));
        model.setName(result.getString("name"));
        return model;
    }

    public List<GenreModel> findAllGenre() {
        String sql = "select * From Genre";
        return jdbcTemplate.query(sql, this::makeGenre);
    }

    private GenreModel makeGenre(ResultSet result, int rowNum) throws SQLException {
        GenreModel genre = new GenreModel();
        genre.setId(result.getInt("id"));
        genre.setName(stringToGenre(result.getString("name")));
        return genre;
    }

    @Override
    public Film findFilmById(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from Film where id = ?;", id);
        if (filmRows.next()) {
            Film film = makeFilm(filmRows);
            List<Map<String, Object>> rowsGenre = jdbcTemplate.queryForList("select genre_id " +
                    "from Film_genre " +
                    "where film_id = ?;", id);
            ArrayList<Integer> filmGenre = new ArrayList<>();
            for (Map row : rowsGenre) {
                filmGenre.add((Integer) row.get("genre_id"));
            }
            film.setGenres(idToGenre(filmGenre));
            List<Map<String, Object>> rowsLikes = jdbcTemplate.queryForList("select user_id from Likes where film_id = ?;", id);
            ArrayList<Integer> filmLikes = new ArrayList<>();
            for (Map row : rowsLikes) {
                filmLikes.add((Integer) row.get("user_id"));
            }
            film.setLikes(new HashSet<>(filmLikes));
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
        film.setMpa(IdtoRating(filmRows.getInt("rating_id")));
        return film;
    }

    private ArrayList<GenreModel> idToGenre(List<Integer> genreId) {
        if (genreId != null) {
            ArrayList<GenreModel> genreFilm = new ArrayList<>();
            for (Integer id : genreId) {
                GenreModel genre = new GenreModel();
                genre.setId(id);
                if (id == 1) {
                    genre.setName(Genre.Комедия);
                } else if (id == 2) {
                    genre.setName(Genre.Драма);
                } else if (id == 3) {
                    genre.setName(Genre.Мультфильм);
                } else if (id == 4) {
                    genre.setName(Genre.Триллер);
                } else if (id == 5) {
                    genre.setName(Genre.Документальный);
                } else if (id == 6) {
                    genre.setName(Genre.Боевик);
                }
                genreFilm.add(genre);
            }
            return genreFilm;
        } else {
            return null;
        }
    }

    private List<Integer> genreToId(ArrayList<GenreModel> genreFilm) {
        if (genreFilm != null) {
            ArrayList<Integer> genreId = new ArrayList<>();
            for (GenreModel genre : genreFilm) {
                genreId.add(genre.getId());
            }
            return genreId;
        } else {
            return null;
        }
    }

    private MpaModel IdtoRating(Integer ratingId) {
        MpaModel model = new MpaModel();
        if (ratingId != null) {
            model.setId(ratingId);
            if (ratingId == 1) {
                model.setName("G");
            } else if (ratingId == 2) {
                model.setName("PG");
            } else if (ratingId == 3) {
                model.setName("PG-13");
            } else if (ratingId == 4) {
                model.setName("R");
            } else if (ratingId == 5) {
                model.setName("NC-17");
            }else{
                throw  new NonexistentException("Такого рейтинга нет!");
            }
            return model;
        }
        throw  new NonexistentException("Такого рейтинга нет!");
    }

    private Genre stringToGenre(String genre) {
        if (genre.equals("Комедия")) {
            return Genre.Комедия;
        } else if (genre.equals("Драма")) {
            return Genre.Драма;
        } else if (genre.equals("Мультфильм")) {
            return Genre.Мультфильм;
        } else if (genre.equals("Триллер")) {
            return Genre.Триллер;
        } else if (genre.equals("Документальный")) {
            return Genre.Документальный;
        } else if (genre.equals("Боевик")) {
            return Genre.Боевик;
        } else {
            return null;
        }
    }

    public MpaModel getMpaById(Integer id) {
        return IdtoRating(id);
    }

    public GenreModel getGenresById(Integer id) {
        GenreModel genre = new GenreModel();
        genre.setId(id);
        if (id == 1) {
            genre.setName(Genre.Комедия);
        } else if (id == 2) {
            genre.setName(Genre.Драма);
        } else if (id == 3) {
            genre.setName(Genre.Мультфильм);
        } else if (id == 4) {
            genre.setName(Genre.Триллер);
        } else if (id == 5) {
            genre.setName(Genre.Документальный);
        } else if (id == 6) {
            genre.setName(Genre.Боевик);
        } else {
            throw  new NonexistentException("Такого жанра нет!");
        }
        return genre;
    }
}
