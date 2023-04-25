package controller;

import exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import model.Film;
import model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Controller
@Slf4j
public class FilmController {
    private ArrayList<Film> filmId = new ArrayList<>();

    @GetMapping("/films")
    public Collection<Film> findAll() {
        log.info("Текущее количество пользователей: {}", filmId.size());
        return filmId;
    }

    @PostMapping("/films")
    public Film create(@RequestBody Film film) {

        if(film.getName() == null || film.getName().isEmpty() || film.getName().isBlank()){
            log.warn("В FilmController при создании фильма передали неверное имя");
            throw new InvalidNameException("Неверное имя");
        }

        if(film.getDescription().length() > 200){
            log.warn("В FilmController при создании фильма передали описание превышаюшее 200 символов");
            throw new InvalidDescriptionException("Описание слишком большое");
        }
        if(film.getDuration() == null || film.getDuration().isNegative() ){
            log.warn("В FilmController при создании фильма передали отрицательную продолжительность фильма");
            throw new InvalidDurationException("Продолжительность фильма должна быть положительной");
        }
        if (film.getReleaseDate().isBefore(LocalDateTime.of(1895, 12,28,00,00))){
            log.warn("В FilmController при создании фильма передали дату релиза, которая находиться раньше 28 декабря 1895 года");
            throw new InvalidReleaseDateException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        filmId.add(film);
        log.info("добавлен фильм - {}", film);
        return film;
    }

    @PutMapping("/films")
    public Film update(@RequestBody Film film) {
        if(film.getName() == null || film.getName().isEmpty() || film.getName().isBlank()){
            log.warn("В FilmController при обновлении информации о фильме передали неверное имя");
            throw new InvalidNameException("Неверное имя");
        }

        if(film.getDescription().length() > 200){
            log.warn("В FilmController при обновлении информации о фильме передали описание превышаюшее 200 символов");
            throw new InvalidDescriptionException("Описание слишком большое");
        }
        if(film.getDuration() == null || film.getDuration().isNegative() ){
            log.warn("В FilmController при обновлении информации о фильме передали отрицательную продолжительность фильма");
            throw new InvalidDurationException("Продолжительность фильма должна быть положительной");
        }
        if (film.getReleaseDate().isBefore(LocalDateTime.of(1895, 12,28,00,00))){
            log.warn("В FilmController при обновлении информации о фильме передали дату релиза, " +
                    "которая находиться раньше 28 декабря 1895 года");
            throw new InvalidReleaseDateException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        filmId.add(film);
        log.info("обновлена информация о фильме - {}", film);
        return film;
    }
}
