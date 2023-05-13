package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NonexistentException;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(User friend, Integer friendId) {
        if (friendId > 0) {
            for (User user : userStorage.findAll()) {
                if (user.getId() == friendId) {
                    friend.getFriends().add(friendId);
                    user.getFriends().add(friend.getId());
                }
            }
        } else {
            throw new InvalidIdException("Id должно быть положительным числом");
        }
    }

    public void deleteFriend(User user, Integer friendId) {
        user.getFriends().remove(friendId);
    }

    public ArrayList<User> findMutualFriends(User friend, User anotherFriend) {
        ArrayList<User> mutualFriends = new ArrayList<>();
        for (Integer mutualFriendId : friend.getFriends()) {
            if (anotherFriend.getFriends().contains(mutualFriendId)) {
                mutualFriends.add(userStorage.findUserById(mutualFriendId.toString()));
            }
        }
        log.info("общие друзья - {}", mutualFriends);
        return mutualFriends;
    }

    public ArrayList<User> getFriends(Set<Integer> friendsId) {
        ArrayList<User> friendsUser = new ArrayList<>();
        for (Integer id : friendsId) {
            if (id < 0) {
                log.warn("getFriends - Id не положительное");
                throw new NonexistentException("Каждое в id друга должно быть положительным");
            }
            friendsUser.add(userStorage.findUserById(id.toString()));
        }
        return friendsUser;
    }
}
