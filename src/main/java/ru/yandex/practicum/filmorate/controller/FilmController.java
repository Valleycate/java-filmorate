package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @RequestMapping(method = RequestMethod.GET)
    public List<Film> findAll() {
        return new ArrayList<>(filmService.findAll());
    }

    @RequestMapping(method = RequestMethod.POST)
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable Integer id) {
        return filmService.findFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer userId, @PathVariable() Integer id) {
        filmService.addLike(userId, findFilmById(id));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer userId, @PathVariable() Integer id) {
        filmService.deleteLike(userId, findFilmById(id));
    }

    @GetMapping("/popular")
    public List<Film> findTop10Films(@RequestParam(required = false, defaultValue = "10") Integer count) {
        return filmService.findTop10Films(count);
    }
}

