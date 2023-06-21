package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidBirthdayException;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidEmailException;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidLoginException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @GetMapping("/users")
    public List<User> findAll() {
        return new ArrayList<>(userStorage.findAll());
    }

    @PostMapping("/users")
    public User create(@Valid @RequestBody User user) {
        validate(user);
        return userStorage.create(user);
    }

    @PutMapping("/users")
    public User update(@Valid @RequestBody User user) {
        validate(user);
        return userStorage.update(user);
    }

    @GetMapping("/users/{id}")
    public User findUserById(@PathVariable Integer id) {
        return userStorage.findUserById(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer friendId, @PathVariable Integer id) {
        userService.addFriend(findUserById(id), friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.deleteFriend(findUserById(id), friendId);
    }

    @GetMapping("/users/{id}/friends")
    public List<User> findAllFriends(@PathVariable Integer id) {
        return userService.getFriends(findUserById(id).getFriends());
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> findMutualFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.findMutualFriends(findUserById(id), findUserById(otherId));
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
}
