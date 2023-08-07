package ru.yandex.practicum.filmorate.storage.DAO.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NonexistentException;
import ru.yandex.practicum.filmorate.model.MpaModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaModel getMpaModel(int id) {
        MpaModel model = new MpaModel();
        SqlRowSet sql = jdbcTemplate.queryForRowSet("select name, id from MPA where id = ?", id);
        if (sql.next()) {
            model.setName(sql.getString("name"));
            model.setId(id);
        } else {
            throw new NonexistentException("такого рейтинга нет!");
        }
        return model;
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
}
