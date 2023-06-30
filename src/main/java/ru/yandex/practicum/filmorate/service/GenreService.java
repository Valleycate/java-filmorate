package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.GenreModel;
import ru.yandex.practicum.filmorate.storage.DAO.GenreDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    @Autowired
    private final GenreDbStorage genreDbStorage;

    public List<GenreModel> findAllGenre() {
        return genreDbStorage.findAllGenre();
    }

    public GenreModel getGenresById(Integer id) {
        return genreDbStorage.getGenresById(id);
    }
}
