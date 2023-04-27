package ru.yandex.practicum.filmorate.controller;


import ru.yandex.practicum.filmorate.exceptions.InvalidEmailException;
import ru.yandex.practicum.filmorate.exceptions.InvalidLoginException;
import ru.yandex.practicum.filmorate.exceptions.InvalidBirthdayException;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exceptions.NothingToUpdate;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private Map<Integer,User> allUsers = new HashMap<>();
    private Integer idUser = 0;

    @GetMapping("/users")
    public Collection<User> findAll() {
        log.info("Текущее количество пользователей: {}", allUsers.size());
        return allUsers.values();
    }

    @PostMapping("/users")
    public User create(@RequestBody User user) {

        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("В UserController при создании пользователя передали неверный логин");
            throw new InvalidLoginException("Неверный логин");
        }

        if (user.getEmail() == null || user.getEmail().isEmpty() || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("В UserController при создании пользователя передали неверный адрес электронной почты");
            throw new InvalidEmailException("Неверный адрес электронной почты");
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("В UserController при создании пользователя передали неверную дату рождения");
            throw new InvalidBirthdayException("Дата рождения не может быть в будущем");
        }
        idUser = idUser + 1;
        allUsers.put(idUser, user);
        user.setId(idUser);
        log.info("добавлен пользователь - {}", user);
        return user;
    }

    @PutMapping("/users")
    public User update(@RequestBody User user) {
        if (user != null && allUsers.containsKey(user.getId())) {
            if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
                log.warn("В UserController при обновлении информации о пользователе передали неверный логин");
                throw new InvalidLoginException("Неверный логин");
            }
            if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            if (user.getEmail() == null || user.getEmail().isEmpty() || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
                log.warn("В UserController при обновлении информации о пользователе передали неверный адрес электронной почты");
                throw new InvalidEmailException("Неверный адрес электронной почты");
            }
            if (user.getBirthday().isAfter(LocalDate.now())) {
                log.warn("В UserController при создании пользователя передали неверную дату рождения");
                throw new InvalidBirthdayException("Дата рождения не может быть в будущем");
            }
            allUsers.put(user.getId(), user);
            log.info("обновлена информация о пользователе - {}", user);
            return user;
        } else {
            log.warn("В UserController при обновлении информации о пользователе передали нового пользователя");
            throw new NothingToUpdate("Такого пользователя нет!");
        }
    }
}
