package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.NonexistentException;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidDescriptionException;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidDurationException;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidNameException;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidReleaseDateException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> allFilms = new HashMap<>();
    private Integer idFilm = 0;

    public List<Film> findAll() {
        log.info("Текущее количество фильмов: {}", allFilms.size());
        log.info("Текущие фильмы: {}", allFilms);
        return new ArrayList<>(allFilms.values());
    }

    private void validate(Film film) {
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
    }

    public Film findFilmById(int id) {
        Optional<Film> optionalFilm = allFilms.values().stream()
                .filter(f -> f.getId() == id)
                .findFirst();
        if (optionalFilm.isPresent()) {
            return optionalFilm.get();
        } else {
            log.warn("findFilmById - фильм не найден; все фильмы - {}", findAll());
            throw new NonexistentException("Фильма с таким id нет");
        }
    }

    public Film create(@Valid @RequestBody Film film) {
        validate(film);
        idFilm = idFilm + 1;
        allFilms.put(idFilm, film);
        film.setId(idFilm);
        log.info("добавлен фильм - {}", film);
        log.info("все фильмы - {}", findAll());
        return film;
    }

    public Film update(@Valid @RequestBody Film film) {
        if (film != null && allFilms.containsKey(film.getId())) {
            validate(film);
            allFilms.put(film.getId(), film);
            log.info("обновлена информация о фильме - {}", film);
            return film;
        } else {
            log.warn("В InMemoryFilmStorage при обновлении информации о фильме передали новый фильм");
            throw new NonexistentException("Такого фильма нет!");
        }
    }
}
