package ru.yandex.practicum.filmorate.storage;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;

public interface UserStorage {
    public Collection<User> findAll();

    public User create(@Valid @RequestBody User user);

    public User update(@Valid @RequestBody User user);

    public User findUserById(String id);
}
