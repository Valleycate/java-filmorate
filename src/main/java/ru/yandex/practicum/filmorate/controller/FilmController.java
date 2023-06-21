package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidDescriptionException;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidDurationException;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidNameException;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidReleaseDateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.GenreModel;
import ru.yandex.practicum.filmorate.model.MpaModel;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.DAO.FilmDbStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmDbStorage filmStorage;
    private final FilmService filmService;

    @GetMapping("/films")
    public List<Film> findAll() {
        return new ArrayList<>(filmStorage.findAll());
    }

    @GetMapping("/genres")
    public List<GenreModel> getAllGenres() {
        return filmStorage.findAllGenre();
    }

    @GetMapping("/genres/{id}")
    public GenreModel getGenresById(@PathVariable Integer id) {
        return filmStorage.getGenresById(id);
    }

    @GetMapping("/mpa/{id}")
    public MpaModel getMPAbyId(@PathVariable Integer id) {
        return filmStorage.getMpaById(id);
    }

    @GetMapping("/mpa")
    public List<MpaModel> getAllMPA() {
        return filmStorage.findAllMPA();
    }

    @PostMapping("/films")
    public Film create(@Valid @RequestBody Film film) {
        validate(film);
        return filmStorage.create(film);
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        validate(film);
        return filmStorage.update(film);
    }

    @GetMapping("/films/{id}")
    public Film findFilmById(@PathVariable Integer id) {
        return filmStorage.findFilmById(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable Integer userId, @PathVariable() Integer id) {
        filmService.addLike(userId, findFilmById(id));
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer userId, @PathVariable() Integer id) {
        filmService.deleteLike(userId, findFilmById(id));
    }

    @GetMapping("/films/popular")
    public List<Film> findTop10Films(@RequestParam(required = false, defaultValue = "10") Integer count) {
        return filmService.findTop10Films(count);
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
}

