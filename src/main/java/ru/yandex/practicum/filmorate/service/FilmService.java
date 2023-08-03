package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film findFilmById(int id) {
        return filmStorage.findFilmById(id);
    }

    public void addLike(Integer userId, Film film) {
        userStorage.findUserById(userId);
        log.info("Поставлен лайк фильму {}", film);
        film.getLikes().add(userId);
        filmStorage.update(film);
    }

    public void deleteLike(Integer userId, Film film) {
        userStorage.findUserById(userId);
        if (film.getLikes().contains(userId)) {
            film.getLikes().remove(userId);
            log.info("Удалён лайк фильму {}", film);
            filmStorage.update(film);
        }
    }

    public List<Film> findTop10Films(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.<Film>comparingInt(o -> o.getLikes().size())
                        .thenComparing(Film::getId, Comparator.reverseOrder()).reversed()
                )
                .limit(count).collect(Collectors.toList());
    }

    public List<Film> findMutualFilms(Integer userId, Integer friendId) {
        userStorage.findUserById(userId);
        userStorage.findUserById(friendId);
        return filmStorage.findMutualFilms(userId, friendId).stream().sorted(Comparator.<Film>comparingInt(o -> o.getLikes().size())
                .thenComparing(Film::getId, Comparator.reverseOrder()).reversed()
        ).collect(Collectors.toList());
    }
}
