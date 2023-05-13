package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidBirthdayException;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidEmailException;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidIdException;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidLoginException;
import ru.yandex.practicum.filmorate.exceptions.NonexistentException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    static private Map<Integer, User> allUsers = new HashMap<>();
    static private Integer idUser = 0;

    public Collection<User> findAll() {
        log.info("Текущее количество пользователей: {}", allUsers.size());
        return allUsers.values();
    }

    public User create(@Valid @RequestBody User user) {

        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("В InMemoryUserStorage при создании пользователя передали неверный логин");
            throw new InvalidLoginException("Неверный логин");
        }

        if (user.getEmail() == null || user.getEmail().isEmpty() || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("В InMemoryUserStorage при создании пользователя передали неверный адрес электронной почты");
            throw new InvalidEmailException("Неверный адрес электронной почты");
        }
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("В InMemoryUserStorage при создании пользователя передали неверную дату рождения");
            throw new InvalidBirthdayException("Дата рождения не может быть в будущем");
        }
        idUser = idUser + 1;
        allUsers.put(idUser, user);
        user.setId(idUser);
        log.info("добавлен пользователь - {}", user);
        return user;
    }

    public User update(@Valid @RequestBody User user) {
        if (user != null && allUsers.containsKey(user.getId())) {
            if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
                log.warn("В InMemoryUserStorage при обновлении информации о пользователе передали неверный логин");
                throw new InvalidLoginException("Неверный логин");
            }
            if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            if (user.getEmail() == null || user.getEmail().isEmpty() || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
                log.warn("В InMemoryUserStorage при обновлении информации о пользователе передали неверный адрес электронной почты");
                throw new InvalidEmailException("Неверный адрес электронной почты");
            }
            if (user.getBirthday().isAfter(LocalDate.now())) {
                log.warn("В InMemoryUserStorage при создании пользователя передали неверную дату рождения");
                throw new InvalidBirthdayException("Дата рождения не может быть в будущем");
            }
            allUsers.put(user.getId(), user);
            log.info("обновлена информация о пользователе - {}", user);
            return user;
        } else {
            log.warn("В InMemoryUserStorage при обновлении информации о пользователе передали нового пользователя");
            throw new NonexistentException("Такого пользователя нет!");
        }
    }

    public User findUserById(String id) {
        Integer userId;
        try {
            userId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            log.warn("findUserById - id не число");
            throw new InvalidIdException("Id должно быть числом");
        }
        for (User user : findAll()) {
            if (user.getId() == userId) {
                return user;
            }
        }
        log.warn("findUserById - пользователь не найден; все пользователи - {}", findAll());
        throw new NonexistentException("Пользователя с таким id нет");
    }

}
