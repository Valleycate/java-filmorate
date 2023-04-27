import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTest {
    FilmController filmController = new FilmController();

    @Test
    public void shouldCreateFilm() {
        try {
            Film film = null;
            filmController.create(film);
        } catch (RuntimeException ignored) {
        }
        assertEquals(0, filmController.findAll().size());

        try {
            Film film = new Film();
            filmController.create(film);
        } catch (RuntimeException ignored) {
        }
        assertEquals(0, filmController.findAll().size());

        try {
            Film film = new Film();
            film.setName(" ");
            filmController.create(film);
        } catch (RuntimeException e) {
            assertEquals("Неверное имя", e.getMessage());
        }
        assertEquals(0, filmController.findAll().size());

        try {
            Film film = new Film();
            film.setName("Титаник");
            film.setDescription("0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789"
                    + "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789"
                    + "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0");
            filmController.create(film);
        } catch (RuntimeException e) {
            assertEquals("Описание слишком большое", e.getMessage());
        }
        assertEquals(0, filmController.findAll().size());

        try {
            Film film = new Film();
            film.setName("Титаник");
            film.setDescription("Мелодрамма");
            film.setDuration(-10);
            filmController.create(film);
        } catch (RuntimeException e) {
            assertEquals("Продолжительность фильма должна быть положительной", e.getMessage());
        }
        assertEquals(0, filmController.findAll().size());

        try {
            Film film = new Film();
            film.setName("Титаник");
            film.setDescription("Мелодрамма");
            film.setDuration(60);
            film.setReleaseDate(LocalDate.of(1894, 2, 28));
            filmController.create(film);
        } catch (RuntimeException e) {
            assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", e.getMessage());
        }
        assertEquals(0, filmController.findAll().size());

        try {
            Film film = new Film();
            film.setName("Титаник");
            film.setDescription("Мелодрамма");
            film.setDuration(60);
            film.setReleaseDate(LocalDate.of(1999, 2, 28));
            filmController.create(film);
        } catch (RuntimeException ignored) {
        }
        assertEquals(1, filmController.findAll().size());
    }

    @Test
    public void shouldUpdateFilm() {
        Film newFilm = new Film();
        newFilm.setName("Титаник");
        newFilm.setDescription("Мелодрамма");
        newFilm.setDuration(60);
        newFilm.setReleaseDate(LocalDate.of(1999, 2, 28));
        filmController.create(newFilm);
        try {
            Film film = null;
            filmController.update(film);
        } catch (RuntimeException e) {
            assertEquals("Такого фильма нет!", e.getMessage());
        }
        assertEquals(1, filmController.findAll().size());

        try {
            Film film = new Film();
            filmController.update(film);
        } catch (RuntimeException e) {
            assertEquals("Такого фильма нет!", e.getMessage());
        }
        assertEquals(1, filmController.findAll().size());

        try {
            Film film = new Film();
            film.setId(newFilm.getId());
            film.setName(" ");
            filmController.update(film);
        } catch (RuntimeException e) {
            assertEquals("Неверное имя", e.getMessage());
        }
        assertEquals(1, filmController.findAll().size());

        try {
            Film film = new Film();
            film.setId(newFilm.getId());
            film.setName("Титаник");
            film.setDescription("0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789"
                    + "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789"
                    + "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0123456789" + "0");
            filmController.update(film);
        } catch (RuntimeException e) {
            assertEquals("Описание слишком большое", e.getMessage());
        }
        assertEquals(1, filmController.findAll().size());

        try {
            Film film = new Film();
            film.setId(newFilm.getId());
            film.setName("Титаник");
            film.setDescription("Мелодрамма");
            film.setDuration(-10);
            filmController.update(film);
        } catch (RuntimeException e) {
            assertEquals("Продолжительность фильма должна быть положительной", e.getMessage());
        }
        assertEquals(1, filmController.findAll().size());

        try {
            Film film = new Film();
            film.setId(newFilm.getId());
            film.setName("Титаник");
            film.setDescription("Мелодрамма");
            film.setDuration(60);
            film.setReleaseDate(LocalDate.of(1894, 2, 28));
            filmController.update(film);
        } catch (RuntimeException e) {
            assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", e.getMessage());
        }
        assertEquals(1, filmController.findAll().size());

        try {
            Film film = new Film();
            film.setName("Титаник");
            film.setDescription("Мелодрамма");
            film.setDuration(60);
            film.setReleaseDate(LocalDate.of(1999, 2, 28));
            filmController.create(film);
            filmController.update(film);
        } catch (RuntimeException ignored) {
        }
        assertEquals(2, filmController.findAll().size());
    }

}
