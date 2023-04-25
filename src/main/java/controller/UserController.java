package controller;


import exceptions.InvalidEmailException;
import exceptions.InvalidLoginException;
import exceptions.InvalidBirthdayException;
import lombok.extern.slf4j.Slf4j;
import model.User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private ArrayList<User> users = new ArrayList<>();

    @GetMapping("/users")
    public Collection<User> findAll() {
        log.info("Текущее количество пользователей: {}", users.size());
        return users;
    }

    @PostMapping("/users")
    public User create(@RequestBody User user) {

        if(user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().isBlank() || user.getLogin().contains(" ")){
            log.warn("В UserController при создании пользователя передали неверный логин");
            throw new InvalidLoginException("Неверный логин");
        }

        if(user.getEmail() == null || user.getEmail().isEmpty() || user.getEmail().isBlank() || !user.getEmail().contains("@")){
            log.warn("В UserController при создании пользователя передали неверный адрес электронной почты");
            throw new InvalidEmailException("Неверный адрес электронной почты");
        }
        if(user.getName() == null || user.getName().isEmpty() || user.getName().isBlank() ){
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDateTime.now())){
            log.warn("В UserController при создании пользователя передали неверную дату рождения");
            throw new InvalidBirthdayException("Дата рождения не может быть в будущем");
        }
        users.add(user);
        log.info("добавлен пользователь - {}", user);
        return user;
    }

    @PutMapping("/users")
    public User update(@RequestBody User user) {
        if(user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().isBlank() || user.getLogin().contains(" ")){
            log.warn("В UserController при обновлении информации о пользователе передали неверный логин");
            throw new InvalidLoginException("Неверный логин");
        }
        if(user.getName() == null || user.getName().isEmpty() || user.getName().isBlank() ){
            user.setName(user.getLogin());
        }
        if(user.getEmail() == null || user.getEmail().isEmpty() || user.getEmail().isBlank() || !user.getEmail().contains("@")){
            log.warn("В UserController при обновлении информации о пользователе передали неверный адрес электронной почты");
            throw new InvalidEmailException("Неверный адрес электронной почты");
        }
        if (user.getBirthday().isAfter(LocalDateTime.now())){
            log.warn("В UserController при создании пользователя передали неверную дату рождения");
            throw new InvalidBirthdayException("Дата рождения не может быть в будущем");
        }
        users.add(user);
        log.info("обновлена информация о пользователе - {}", user);
        return user;
    }
}
