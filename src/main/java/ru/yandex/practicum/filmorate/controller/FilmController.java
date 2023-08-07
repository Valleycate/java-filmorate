package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping()
    public List<Film> findAll() {
        return new ArrayList<>(filmService.findAll());
    }

    @PostMapping()
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping()
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping("/common")
    public List<Film> findMutualFilm(@RequestParam Integer userId, @RequestParam Integer friendId) {
        return filmService.findMutualFilms(userId, friendId);
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
    public List<Film> findTop10Films(@RequestParam(required = false, defaultValue = "10") Integer count,
                                     @RequestParam(required = false) @Validated @Positive Integer genreId,
                                     @RequestParam(required = false) @Validated @Positive Integer year) {
        return filmService.findTop10Films(count, genreId, year);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Integer id) {
        log.info("A movie delete from database");
        filmService.deleteById(id);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getSortedFilmsOfDirector(@PathVariable("directorId") Integer id, @RequestParam("sortBy") String param) {
        return filmService.getSortedFilmsOfDirector(id, param);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query,
                                  @RequestParam(name = "by", defaultValue = "title") List<String> searchByParams) {
        return filmService.searchFilms(query, searchByParams);
    }
}