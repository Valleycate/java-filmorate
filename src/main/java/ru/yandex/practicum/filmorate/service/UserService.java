package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NonexistentException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DAO.UserDbStorage;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserDbStorage userStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        checkNameUser(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        checkNameUser(user);
        if (userStorage.findUserById(user.getId()) == null) {
            throw new NonexistentException("user not exist with current id");
        }
        return userStorage.update(user);
    }

    public User findUserById(int id) {
        if (id <= 0) {
            throw new NonexistentException("id user <= 0");
        }
        if (userStorage.findUserById(id) == null) {
            throw new NonexistentException("user not exist with current id");
        }
        return userStorage.findUserById(id);
    }

    public void addFriend(User user, User friend) {
        userStorage.addFriend(user, friend);
    }

    public void deleteFriend(User user, User friend) {
        userStorage.deleteFriend(user, friend);
    }

    public List<User> findMutualFriends(Integer id, Integer otherId) {
        if (userStorage.findUserById(id) == null || userStorage.findUserById(otherId) == null) {
            throw new NonexistentException("user not exist with current id");
        }
        return userStorage.getCommonFriends(id, otherId);
    }

    public List<User> getFriends(Integer id) {
        if (userStorage.findUserById(id) == null) {
            throw new NonexistentException("user not exist with current id");
        }
        return userStorage.allFriends(userStorage.findUserById(id));
    }


    public User deleteById(Integer id) {
        if (id <= 0) {
            throw new NonexistentException("incorect if for delete user");
        }
        if (userStorage.findUserById(id) == null) {
            throw new NonexistentException("user not exist with current id");
        }
       return userStorage.deleteById(id);
    }

    private static void checkNameUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
