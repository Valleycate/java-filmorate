package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaModel;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.DAO.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.DAO.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmControllerTest {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmDbStorage;
    private final FilmService filmService;

    public User createUser() {
        User user = new User();
        user.setLogin("some_login");
        user.setEmail("Email.com@Someone");
        user.setName("some_name");
        user.setBirthday(LocalDate.of(2000, 2, 28));
        return userStorage.create(user);
    }

    public Film createFilm() {
        Film film = new Film();
        film.setName("Титаник");
        film.setDescription("Мелодрамма");
        film.setDuration(60);
        film.setReleaseDate(LocalDate.of(1999, 2, 28));
        MpaModel m = new MpaModel();
        m.setId(1);
        m.setName("G");
        film.setGenres(new ArrayList<>());
        film.setMpa(m);
        return filmDbStorage.create(film);
    }

    @Test
    public void testCreateFilm() {
        Film film = new Film();
        film.setName("Титаник");
        film.setDescription("Мелодрамма");
        film.setDuration(60);
        film.setReleaseDate(LocalDate.of(1999, 2, 28));
        MpaModel m = new MpaModel();
        m.setId(1);
        m.setName("G");
        film.setGenres(new ArrayList<>());
        film.setMpa(m);
        film = filmDbStorage.create(film);
        assertEquals(film, filmDbStorage.findFilmById(film.getId()));
    }

    @Test
    public void testUpdateFilm() {
        Film film = createFilm();
        film.setDuration(120);
        filmDbStorage.update(film);
        assertEquals(filmDbStorage.findFilmById(film.getId()).getDuration(), 120);
    }

    @Test
    public void testFindFilmById() {
        createFilm();
        Film film = filmDbStorage.findFilmById(1);
        assertEquals(film.getId(), 1);
    }

    @Test
    public void testFindAllFilms() {
        createFilm();
        createFilm();
        assertNotEquals(filmDbStorage.findAll().size(), 1);
    }

    @Test
    public void testAddLike() {
        Film film = createFilm();
        User user = createUser();
        filmService.addLike(user.getId(), film);
        assertEquals(film.getLikes().size(), 1);
        assertEquals(film.getLikes(), Set.of(user.getId()));
    }

    @Test
    public void testDeleteLike() {
        Film film = createFilm();
        User user = createUser();
        filmService.addLike(user.getId(), film);
        filmService.deleteLike(user.getId(), film);
        assertEquals(film.getLikes().size(), 0);
    }
}
