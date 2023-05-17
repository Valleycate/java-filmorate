package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.NonexistentException;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidBirthdayException;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidEmailException;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidLoginException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> allUsers = new HashMap<>();
    private Integer idUser = 0;

    public List<User> findAll() {
        log.info("Текущее количество пользователей: {}", allUsers.size());
        return new ArrayList<>(allUsers.values());
    }

    private void validate(User user) {
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
    }

    public User create(@Valid @RequestBody User user) {
        validate(user);
        idUser = idUser + 1;
        allUsers.put(idUser, user);
        user.setId(idUser);
        log.info("добавлен пользователь - {}", user);
        return user;
    }

    public User update(@Valid @RequestBody User user) {
        if (user != null && allUsers.containsKey(user.getId())) {
            validate(user);
            allUsers.put(user.getId(), user);
            log.info("обновлена информация о пользователе - {}", user);
            return user;
        } else {
            log.warn("В InMemoryUserStorage при обновлении информации о пользователе передали нового пользователя");
            throw new NonexistentException("Такого пользователя нет!");
        }
    }

    public User findUserById(int id) {
        for (User user : findAll()) {
            if (user.getId() == id) {
                return user;
            }
        }
        log.warn("findUserById - пользователь не найден; все пользователи - {}", findAll());
        throw new NonexistentException("Пользователя с таким id нет");
    }

}
