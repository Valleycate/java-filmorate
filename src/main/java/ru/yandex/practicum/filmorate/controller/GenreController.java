package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.GenreModel;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GenreController {
    @Autowired
    private final GenreService genreService;

    @GetMapping("/genres")
    public List<GenreModel> getAllGenres() {
        return genreService.findAllGenre();
    }

    @GetMapping("/genres/{id}")
    public GenreModel getGenresById(@PathVariable Integer id) {
        return genreService.getGenresById(id);
    }

}
