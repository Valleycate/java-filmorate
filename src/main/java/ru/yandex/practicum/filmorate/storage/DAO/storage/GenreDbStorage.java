package ru.yandex.practicum.filmorate.storage.DAO.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NonexistentException;
import ru.yandex.practicum.filmorate.model.GenreModel;
import ru.yandex.practicum.filmorate.storage.DAO.Interface.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    protected List<GenreModel> updateGenre(ArrayList<GenreModel> genreFilm, int filmId) {
        if (genreFilm != null) {
            jdbcTemplate.update("DELETE FROM FILM_GENRE WHERE film_id =?;", filmId);
            Set<Integer> genreId = new HashSet<>();
            for (GenreModel genre : genreFilm) {
                genreId.add(genre.getId());
            }
            ArrayList<GenreModel> genres = new ArrayList<>();
            for (Integer id : genreId) {
                jdbcTemplate.update("INSERT INTO Film_genre (film_id, genre_id) VALUES(?,?);", filmId, id);
                genres.add(getGenresById(id));
            }
            return genres;
        }
        return new ArrayList<>();
    }

    protected List<GenreModel> getGenresFilm(int filmId) {
        List<Map<String, Object>> rowsGenre = jdbcTemplate.queryForList("select genre_id " +
                "from Film_genre " +
                "where film_id = ?;", filmId);
        ArrayList<GenreModel> filmGenre = new ArrayList<>();
        for (Map<String, Object> row : rowsGenre) {
            GenreModel genre = getGenresById((Integer) row.get("genre_id"));
            filmGenre.add(genre);
        }
        return filmGenre;
    }

    @Override
    public List<GenreModel> findAllGenre() {
        String sql = "select * From Genre";
        return jdbcTemplate.query(sql, this::makeGenre);
    }

    private GenreModel makeGenre(ResultSet result, int rowNum) throws SQLException {
        GenreModel genre = new GenreModel();
        genre.setId(result.getInt("id"));
        genre.setName(result.getString("name"));
        return genre;
    }

    @Override
    public GenreModel getGenresById(Integer id) {
        SqlRowSet sql = jdbcTemplate.queryForRowSet("Select name from Genre where id = ?", id);
        GenreModel genre = new GenreModel();
        if (sql.next()) {
            genre.setName(sql.getString("name"));
        } else {
            throw new NonexistentException("Такого жанра нет!");
        }
        genre.setId(id);
        return genre;
    }
}
