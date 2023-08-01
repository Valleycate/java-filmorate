package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public List<User> findAll() {
        return new ArrayList<>(userService.findAll());
    }

    @PostMapping()
    public User create(@Valid @RequestBody User user) {
        user.validateName();
        return userService.create(user);
    }

    @PutMapping()
    public User update(@Valid @RequestBody User user) {
        user.validateName();
        return userService.update(user);
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable Integer id) {
        return userService.findUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer friendId, @PathVariable Integer id) {
        userService.addFriend(findUserById(id), friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.deleteFriend(findUserById(id), friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findAllFriends(@PathVariable Integer id) {
        return userService.getFriends(findUserById(id).getFriends());
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findMutualFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.findMutualFriends(findUserById(id), findUserById(otherId));
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        userService.deleteById(id);
    }

}
