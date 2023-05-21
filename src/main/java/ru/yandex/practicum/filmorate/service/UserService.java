package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NonexistentException;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidIdException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public void addFriend(User user, Integer friendId) {
        if (friendId >= 0) {
            User friend = userStorage.findAll().stream()
                    .filter(u -> u.getId() == friendId)
                    .findFirst()
                    .orElseThrow(NonexistentException::new);
            friend.getFriends().add(user.getId());
            user.getFriends().add(friend.getId());
        } else {
            throw new InvalidIdException("Id друга не положительное");
        }
    }

    public void deleteFriend(User user, Integer friendId) {
        user.getFriends().remove(friendId);
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
