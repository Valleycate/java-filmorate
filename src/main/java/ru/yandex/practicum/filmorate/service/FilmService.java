package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NonexistentException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.EnumEventType;
import ru.yandex.practicum.filmorate.model.enums.EnumOperation;
import ru.yandex.practicum.filmorate.service.util.FeedSaver;
import ru.yandex.practicum.filmorate.storage.DAO.Interface.FilmStorage;
import ru.yandex.practicum.filmorate.storage.DAO.Interface.GenreStorage;
import ru.yandex.practicum.filmorate.storage.DAO.Interface.UserStorage;
import ru.yandex.practicum.filmorate.storage.DAO.storage.DirectorDbStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final DirectorDbStorage directorDbStorage;
    private final GenreStorage genreStorage;
    private final FeedSaver feedSaver;

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
        feedSaver.saveFeed(userId, (long) film.getId(), EnumEventType.LIKE, EnumOperation.ADD);
    }

    public void deleteLike(Integer userId, Film film) {
        if (userStorage.findUserById(userId) == null) {
            throw new NonexistentException("user not exist with current id");
        }
        userStorage.findUserById(userId);
        if (film.getLikes().contains(userId)) {
            film.getLikes().remove(userId);
            log.info("Удалён лайк фильму {}", film);
            filmStorage.update(film);
        }
        feedSaver.saveFeed(userId, (long) film.getId(), EnumEventType.LIKE, EnumOperation.REMOVE);
    }

    public List<Film> findTop10Films(int count, Integer genreId, Integer year) {
        if (genreId != null && genreStorage.getGenresById(genreId) == null) {
            throw new NonexistentException("not exist genres with current id");
        }
        return filmStorage.findTop10Films(count, genreId, year);
    }

    public List<Film> findMutualFilms(Integer userId, Integer friendId) {
        userStorage.findUserById(userId);
        userStorage.findUserById(friendId);
        return getSortedFilmsByLikes(filmStorage.findMutualFilms(userId, friendId));
    }

    public void deleteById(Integer id) {
        if (filmStorage.findFilmById(id) == null) {
            throw new NonexistentException("Film by id  not exist");
        }
        filmStorage.deleteById(id);
    }

    public List<Film> getSortedFilmsOfDirector(int directorId, String param) {
        if (directorDbStorage.getDirector(directorId) != null) {
            switch (param) {
                case "year":
                    return filmStorage.getSortedByYearFilmsOfDirector(directorId);
                case "likes":
                    return getSortedFilmsByLikes(filmStorage.getDirectorsFilms(directorId));
                default:
                    return null;
            }
        } else {
            return null;
        }
    }

    public List<Film> getRecommendations(int userId, int friendId) {
        List<Film> recommendations = filmStorage.getRecommendations(userId, friendId);
        return recommendations.stream()
                .filter(film -> film.getLikes().contains(friendId) && !film.getLikes().contains(userId))
                .collect(Collectors.toList());
    }

    public List<Film> searchFilms(String query, List<String> searchByParams) {
        return filmStorage.searchFilms(query, searchByParams);
    }

    private List<Film> getSortedFilmsByLikes(List<Film> filmsList) {
        return filmsList.stream()
                .sorted(Comparator.<Film>comparingInt(o -> o.getLikes().size())
                        .thenComparing(Film::getId, Comparator.reverseOrder()).reversed()
                )
                .collect(Collectors.toList());
    }
}
