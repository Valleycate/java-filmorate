package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(@Valid @RequestBody Film film);

    Film update(@Valid @RequestBody Film film);

    Film findFilmById(int id);
}
