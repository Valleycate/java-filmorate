package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;


@RestController
public class FilmController {
    private InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
    public FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @PostMapping("/films")
    public Film create(@Valid @RequestBody Film film) {
        return filmStorage.create(film);
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        return filmStorage.update(film);
    }

    @GetMapping("/films/{id}")
    public Film findFilmById(@PathVariable String id) {
        return filmStorage.findFilmById(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable Integer userId, @PathVariable() String id) {
        filmService.addLike(userId, findFilmById(id));
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer userId, @PathVariable() String id) {
        filmService.deleteLike(userId, findFilmById(id));
    }

    @GetMapping("/films/popular")
    public ArrayList<Film> findTop10Films(@RequestParam(required = false, defaultValue = "10") Integer count) {
        return filmService.findTop10Films(count);
    }
}
