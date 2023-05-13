package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;

public interface FilmStorage {
    public Collection<Film> findAll();

    public Film create(@Valid @RequestBody Film film);

    public Film update(@Valid @RequestBody Film film);

    public Film findFilmById(String id);
}
