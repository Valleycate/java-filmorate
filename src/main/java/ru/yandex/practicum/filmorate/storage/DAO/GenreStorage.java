package ru.yandex.practicum.filmorate.storage.DAO;


import ru.yandex.practicum.filmorate.model.GenreModel;

import java.util.List;

public interface GenreStorage {

    List<GenreModel> findAllGenre();

    GenreModel getGenresById(Integer id);
}

