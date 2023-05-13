package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;

@RestController
@Slf4j
public class UserController {
    InMemoryUserStorage userStorage = new InMemoryUserStorage();
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Collection<User> findAll() {
        return userStorage.findAll();
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
    public User findUserById(@PathVariable String id) {
        return userStorage.findUserById(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer friendId, @PathVariable String id) {
        userService.addFriend(findUserById(id), friendId);
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable String id, @PathVariable Integer friendId) {
        userService.deleteFriend(findUserById(id), friendId);
    }

    @GetMapping("/users/{id}/friends")
    public ArrayList<User> findAllFriends(@PathVariable String id) {
        return userService.getFriends(findUserById(id).getFriends());
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public ArrayList<User> findMutualFriends(@PathVariable String id, @PathVariable String otherId) {
        return userService.findMutualFriends(findUserById(id), findUserById(otherId));
    }


}
