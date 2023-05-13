package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Slf4j
public class FilmService {
    UserStorage userStorage;
    FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Integer userId, Film film) {
        userStorage.findUserById(userId.toString());
        log.info("Поставлен лайк фильму {}", film);
        film.getLike().put(userId, true);
    }

    public void deleteLike(Integer userId, Film film) {
        userStorage.findUserById(userId.toString());
        log.info("Поставлен лайк фильму {}", film);
        film.getLike().remove(userId);
    }

    public ArrayList<Film> findTop10Films(int count) {
        ArrayList<Film> top = new ArrayList<>();
        Film test = null;
        Collection<Film> allFilm = filmStorage.findAll();
        for (int i = 0; i < count; i++) {
            int maxLike = 0;
            for (Film film : allFilm) {
                if (!top.contains(film) && film.getLike() != null && film.getLike().size() >= maxLike) {
                    maxLike = film.getLike().size();
                    test = film;
                }
            }
            if (test != null && !top.contains(test)) {
                top.add(test);
            }
        }
        log.info("топ фильмов - {}", top);
        return top;
    }
}
