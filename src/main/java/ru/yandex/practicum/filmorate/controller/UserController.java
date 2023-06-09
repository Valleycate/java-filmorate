package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final InMemoryUserStorage userStorage;
    private final UserService userService;

    @GetMapping("/users")
    public List<User> findAll() {
        return new ArrayList<>(userStorage.findAll());
    }

    @PostMapping("/users")
    public User create(@Valid @RequestBody User user) {
        return userStorage.create(user);
    }

    @PutMapping("/users")
    public User update(@Valid @RequestBody User user) {
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


}
