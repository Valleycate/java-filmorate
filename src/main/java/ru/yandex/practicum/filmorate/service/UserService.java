package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NonexistentException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public void addFriend(User user, Integer friendId) {
        Optional<User> optionalFriend = userStorage.findAll().stream().filter(u -> u.getId() == friendId)
                .findFirst();
        if (optionalFriend.isPresent()) {
            User friend = optionalFriend.get();
            friend.getFriends().add(user.getId());
            user.getFriends().add(friend.getId());
        } else {
            throw new NonexistentException("user not existed");
        }
    }

    public void deleteFriend(User user, Integer friendId) {
        user.getFriends().remove(friendId);
    }

    public ArrayList<User> findMutualFriends(User friend, User anotherFriend) {
        ArrayList<User> mutualFriends = new ArrayList<>();
        List<User> list = friend.getFriends().stream().filter(fid -> anotherFriend.getFriends().contains(fid))
                .map(userStorage::findUserById).collect(Collectors.toList());
        for (Integer mutualFriendId : friend.getFriends()) {
            if (anotherFriend.getFriends().contains(mutualFriendId)) {
                mutualFriends.add(userStorage.findUserById(mutualFriendId));
            }
        }
        log.info("общие друзья - {}", mutualFriends);
        return mutualFriends;
    }

    public List<User> getFriends(Set<Integer> userId) {
        return userId.stream().map(userStorage::findUserById).collect(Collectors.toList());
    }
}
