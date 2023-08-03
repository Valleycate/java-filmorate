package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;

public interface FilmStorage {
    List<Film> findAll();

    Film create(@Valid @RequestBody Film film);

    Film update(@Valid @RequestBody Film film);

    Film findFilmById(int id);

    Film deleteById(Integer id);


    List<Film> findMutualFilms(int userId, int friendId);
}
