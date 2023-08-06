package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NonexistentException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.EnumEventType;
import ru.yandex.practicum.filmorate.model.enums.EnumOperation;
import ru.yandex.practicum.filmorate.storage.DAO.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.DAO.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.DAO.GenreStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FeedDbStorage feedStorage;
    private final DirectorDbStorage directorDbStorage;
    private final GenreStorage genreStorage;

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
        feedStorage.save(Feed.builder()
                .userId(userId)
                .entityId((long) film.getId())
                .eventType(EnumEventType.LIKE)
                .operation(EnumOperation.ADD)
                .timestamp(Instant.now().toEpochMilli())
                .build());
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
        feedStorage.save(Feed.builder()
                .userId(userId)
                .entityId((long) film.getId())
                .eventType(EnumEventType.LIKE)
                .operation(EnumOperation.REMOVE)
                .timestamp(Instant.now().toEpochMilli())
                .build());
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
        return filmStorage.findMutualFilms(userId, friendId).stream().sorted(Comparator.<Film>comparingInt(o -> o.getLikes().size())
                .thenComparing(Film::getId, Comparator.reverseOrder()).reversed()
        ).collect(Collectors.toList());
    }

    public void deleteById(Integer id) {
        if (filmStorage.findFilmById(id) == null) {
            throw new NonexistentException("Film by id  not exist");
        }
        filmStorage.deleteById(id);
    }

    public List<Film> sortedFilmsOfDirector(int directorId, String param) {
        if (directorDbStorage.getDirector(directorId) != null) {
            switch (param) {
                case "year":
                    return filmStorage.getSortedByYearFilmsOfDirector(directorId);
                case "likes":
                    return filmStorage.getDirectorsFilms(directorId).stream()
                            .sorted(Comparator.<Film>comparingInt(o -> o.getLikes().size())
                                    .thenComparing(Film::getId, Comparator.reverseOrder()).reversed()
                            )
                            .collect(Collectors.toList());
                default:
                    return null;
            }
        } else {
            return null;
        }
    }

    public List<Film> recommendations(int userId, int friendId) {
        List<Film> films = filmStorage.recommendations(userId, friendId);
        List<Film> recommendations = new ArrayList<>();
        for (Film film : films) {
            if (film.getLikes().contains(friendId) && !film.getLikes().contains(userId)) {
                recommendations.add(film);
            }
        }
        return recommendations;
    }
}
