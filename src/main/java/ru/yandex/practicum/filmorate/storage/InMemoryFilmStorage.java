package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.NonexistentException;
import ru.yandex.practicum.filmorate.exceptions.validationException.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    static private final Map<Integer, Film> allFilms = new HashMap<>();
    static Integer idFilm = 0;

    public Collection<Film> findAll() {
        log.info("Текущее количество фильмов: {}", allFilms.size());
        log.info("Текущие фильмы: {}", allFilms);
        return allFilms.values();
    }

    public Film findFilmById(String id) {
        Integer filmId;
        try {
            filmId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            log.warn("findFilmById - id не число");
            throw new InvalidIdException("Id должно быть числом");
        }
        for (Film film : allFilms.values()) {
            if (film.getId() == filmId) {
                return film;
            }
        }
        log.warn("findFilmById - фильм не найден; все фильмы - {}", findAll());
        throw new NonexistentException("Фильма с таким id нет");
    }

    public Film create(@Valid @RequestBody Film film) {

        if (film.getName() == null || film.getName().isEmpty() || film.getName().isBlank()) {
            log.warn("В InMemoryFilmStorage при создании фильма передали неверное имя");
            throw new InvalidNameException("Неверное имя");
        }

        if (film.getDescription().length() > 200) {
            log.warn("В InMemoryFilmStorage при создании фильма передали описание превышаюшее 200 символов");
            throw new InvalidDescriptionException("Описание слишком большое");
        }
        if (film.getDuration() == null || film.getDuration() < 0) {
            log.warn("В InMemoryFilmStorage при создании фильма передали отрицательную продолжительность фильма");
            throw new InvalidDurationException("Продолжительность фильма должна быть положительной");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("В InMemoryFilmStorage при создании фильма передали дату релиза, которая находиться раньше 28 декабря 1895 года");
            throw new InvalidReleaseDateException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        idFilm = idFilm + 1;
        allFilms.put(idFilm, film);
        film.setId(idFilm);
        log.info("добавлен фильм - {}", film);
        log.info("все фильмы - {}", findAll());
        return film;
    }

    public Film update(@Valid @RequestBody Film film) {
        if (film != null && allFilms.containsKey(film.getId())) {
            if (film.getName() == null || film.getName().isEmpty() || film.getName().isBlank()) {
                log.warn("В InMemoryFilmStorage при обновлении информации о фильме передали неверное имя");
                throw new InvalidNameException("Неверное имя");
            }

            if (film.getDescription().length() > 200) {
                log.warn("В InMemoryFilmStorage при обновлении информации о фильме передали описание превышаюшее 200 символов");
                throw new InvalidDescriptionException("Описание слишком большое");
            }
            if (film.getDuration() == null || film.getDuration() < 0) {
                log.warn("В InMemoryFilmStorage при обновлении информации о фильме передали отрицательную продолжительность фильма");
                throw new InvalidDurationException("Продолжительность фильма должна быть положительной");
            }
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.warn("В InMemoryFilmStorage при обновлении информации о фильме передали дату релиза, " +
                        "которая находиться раньше 28 декабря 1895 года");
                throw new InvalidReleaseDateException("Дата релиза не может быть раньше 28 декабря 1895 года");
            }
            allFilms.put(film.getId(), film);
            log.info("обновлена информация о фильме - {}", film);
            return film;
        } else {
            log.warn("В InMemoryFilmStorage при обновлении информации о фильме передали новый фильм");
            throw new NonexistentException("Такого фильма нет!");
        }
    }
}
