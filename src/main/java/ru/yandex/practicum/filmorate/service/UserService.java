package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NonexistentException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EnumEventType;
import ru.yandex.practicum.filmorate.model.enums.EnumOperation;
import ru.yandex.practicum.filmorate.service.util.FeedSaver;
import ru.yandex.practicum.filmorate.storage.DAO.storage.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.DAO.storage.UserDbStorage;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserDbStorage userStorage;
    private final FeedDbStorage feedStorage;
    private final FilmService filmService;
    private final FeedSaver feedSaver;

    private void checkNameUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

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
        feedSaver.saveFeed(user.getId(), (long) friend.getId(), EnumEventType.FRIEND, EnumOperation.ADD);
    }

    public void deleteFriend(User user, User friend) {
        userStorage.deleteFriend(user, friend);
        feedSaver.saveFeed(user.getId(), (long) friend.getId(), EnumEventType.FRIEND, EnumOperation.REMOVE);
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

    public List<Film> getRecommendations(int userId) {
        findUserById(userId);
        int id = -1;
        int max = 0;
        for (User user : userStorage.findAll()) {
            if (user.getId() != userId) {
                int size = filmService.findMutualFilms(userId, user.getId()).size();
                if (size > max) {
                    id = user.getId();
                    max = size;
                }
            }
        }
        if (id == -1) {
            return new ArrayList<>();
        }
        return filmService.getRecommendations(userId, id);
    }

    public List<Feed> getFeed(Integer userId) {
        findUserById(userId);
        return feedStorage.findUsersFeed(userId);
    }
}
