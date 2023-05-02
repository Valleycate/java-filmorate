package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
@Slf4j
public class FilmController {
    private Map<Integer, Film> allFilms = new HashMap();
    Integer idFilm = 0;

    @GetMapping("/films")
    public Collection<Film> findAll() {
        log.info("Текущее количество фильмов: {}", allFilms.size());
        return allFilms.values();
    }

    @PostMapping("/films")
    public Film create(@Valid @RequestBody Film film) {

        if (film.getName() == null || film.getName().isEmpty() || film.getName().isBlank()) {
            log.warn("В FilmController при создании фильма передали неверное имя");
            throw new InvalidNameException("Неверное имя");
        }

        if (film.getDescription().length() > 200) {
            log.warn("В FilmController при создании фильма передали описание превышаюшее 200 символов");
            throw new InvalidDescriptionException("Описание слишком большое");
        }
        if (film.getDuration() == null || film.getDuration() < 0) {
            log.warn("В FilmController при создании фильма передали отрицательную продолжительность фильма");
            throw new InvalidDurationException("Продолжительность фильма должна быть положительной");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("В FilmController при создании фильма передали дату релиза, которая находиться раньше 28 декабря 1895 года");
            throw new InvalidReleaseDateException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        idFilm = idFilm + 1;
        allFilms.put(idFilm, film);
        film.setId(idFilm);
        log.info("добавлен фильм - {}", film);
        return film;
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        if (film != null && allFilms.containsKey(film.getId())) {
            if (film.getName() == null || film.getName().isEmpty() || film.getName().isBlank()) {
                log.warn("В FilmController при обновлении информации о фильме передали неверное имя");
                throw new InvalidNameException("Неверное имя");
            }

            if (film.getDescription().length() > 200) {
                log.warn("В FilmController при обновлении информации о фильме передали описание превышаюшее 200 символов");
                throw new InvalidDescriptionException("Описание слишком большое");
            }
            if (film.getDuration() == null || film.getDuration() < 0) {
                log.warn("В FilmController при обновлении информации о фильме передали отрицательную продолжительность фильма");
                throw new InvalidDurationException("Продолжительность фильма должна быть положительной");
            }
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.warn("В FilmController при обновлении информации о фильме передали дату релиза, " +
                        "которая находиться раньше 28 декабря 1895 года");
                throw new InvalidReleaseDateException("Дата релиза не может быть раньше 28 декабря 1895 года");
            }
            allFilms.put(film.getId(), film);
            log.info("обновлена информация о фильме - {}", film);
            return film;
        } else {
            log.warn("В FilmController при обновлении информации о фильме передали новый фильм");
            throw new NothingToUpdate("Такого фильма нет!");
        }
    }
}
