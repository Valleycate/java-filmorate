package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User create(@Valid @RequestBody User user);

    User update(@Valid @RequestBody User user);

    User findUserById(int id);
}
