package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.GenreModel;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @RequestMapping(method = RequestMethod.GET)
    public List<GenreModel> getAllGenres() {
        return genreService.findAllGenre();
    }

    @GetMapping("/{id}")
    public GenreModel getGenresById(@PathVariable Integer id) {
        return genreService.getGenresById(id);
    }

}
