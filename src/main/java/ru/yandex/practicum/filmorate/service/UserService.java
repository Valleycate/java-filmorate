package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NonexistentException;
import ru.yandex.practicum.filmorate.exceptions.validationException.BadRequest;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DAO.UserDbStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserDbStorage userStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User findUserById(int id) {
        return userStorage.findUserById(id);
    }

    public void addFriend(User user, Integer friendId) {
        if (friendId >= 0) {
            User friend = userStorage.findAll().stream()
                    .filter(u -> u.getId() == friendId)
                    .findFirst()
                    .orElseThrow(NonexistentException::new);
            user.getFriends().add(friend.getId());
            if (friend.getFriendship().get(user.getId()) == Friendship.UNCONFIRMED) {
                user.getFriendship().put(friend.getId(), Friendship.CONFIRMED);
                friend.getFriendship().put(user.getId(), Friendship.CONFIRMED);
            } else {
                user.getFriendship().put(friend.getId(), Friendship.UNCONFIRMED);
            }
            userStorage.update(user);
        } else {
            throw new BadRequest("Id друга не положительное");
        }
    }

    public void deleteFriend(User user, Integer friendId) {
        user.getFriends().remove(friendId);
        user.getFriendship().remove(friendId);
        userStorage.deleteFriend(user.getId(), friendId);
        userStorage.findUserById(friendId).getFriends().remove(user.getId());
        userStorage.findUserById(friendId).getFriendship().remove(user.getId());
        userStorage.deleteFriend(friendId, user.getId());

    }

    public List<User> findMutualFriends(User friend, User anotherFriend) {
        List<User> list = friend.getFriends().stream()
                .filter(fid -> anotherFriend.getFriends().contains(fid))
                .map(userStorage::findUserById)
                .collect(Collectors.toList());
        log.info("общие друзья - {}", list);
        return list;
    }

    public List<User> getFriends(Set<Integer> userId) {
        return userId.stream()
                .map(userStorage::findUserById)
                .collect(Collectors.toList());
    }
}
