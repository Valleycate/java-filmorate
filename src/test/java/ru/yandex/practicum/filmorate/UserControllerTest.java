package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.DAO.UserDbStorage;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {
    private final UserDbStorage userStorage;
    private final UserService userService;
    public User createUser() {
        User user = new User();
        user.setLogin("some_login");
        user.setEmail("Email.com@Someone");
        user.setName("some_name");
        user.setFriendship(new HashMap<>());
        user.setBirthday(LocalDate.of(2000, 2, 28));
        return userStorage.create(user);
    }

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setLogin("some_login");
        user.setEmail("Email.com@Someone");
        user.setName("some_name");
        user.setFriendship(new HashMap<>());
        user.setBirthday(LocalDate.of(2000, 2, 28));
        user = userStorage.create(user);
        assertEquals(user, userStorage.findUserById(user.getId()));
    }

    @Test
    public void testFindUserById() {
        User user = userStorage.findUserById(1);
        assertEquals(user.getId(), 1);
    }

    @Test
    public void testFindAll() {
        createUser();
        createUser();
        List<User> users = userStorage.findAll();
        assertNotEquals(users.size(), 1);
    }

    @Test
    public void testUpdateUser() {
        User user = createUser();
        user.setName("AAAA");
        user = userStorage.update(user);
        assertEquals(user.getName(), "AAAA");
    }

    @Test
    public void testAddFriend() {
        User user = createUser();
        User friend = createUser();
        userService.addFriend(user, friend.getId());
        assertEquals(user.getFriends().size(), 1);
        assertEquals(user.getFriends(), Set.of(friend.getId()));
        assertEquals(user.getFriendship().size(), 1);
        assertEquals(user.getFriendship().get(friend.getId()), Friendship.UNCONFIRMED);
        assertEquals(friend.getFriends().size(), 0);
        userService.addFriend(friend, user.getId());
        assertEquals(friend.getFriends().size(), 1);
        assertEquals(friend.getFriends(), Set.of(user.getId()));
        assertEquals(friend.getFriendship().size(), 1);
        assertEquals(friend.getFriendship().get(user.getId()), Friendship.CONFIRMED);
    }

    @Test
    public void testDeleteFriend() {
        User user = createUser();
        User friend = createUser();
        userService.addFriend(user, friend.getId());
        userService.addFriend(friend, user.getId());
        userService.deleteFriend(user, friend.getId());
        assertEquals(user.getFriends().size(), 0);
        assertEquals(friend.getFriends().size(), 1);
    }

    @Test
    public void testFindAllFriends() {
        User user = createUser();
        User friend = createUser();
        User friend2 = createUser();
        userService.addFriend(user, friend.getId());
        userService.addFriend(friend, user.getId());
        userService.addFriend(user, friend2.getId());
        userService.addFriend(friend2, user.getId());
        List<User> friends = userService.getFriends(user.getFriends());
        assertEquals(friends.get(0), friend);
        assertEquals(friends.get(1), friend2);
    }

    @Test
    public void testFindMutualFriends() {
        User user = createUser();
        User friend = createUser();
        User friend2 = createUser();
        userService.addFriend(user, friend.getId());
        userService.addFriend(friend, user.getId());
        userService.addFriend(user, friend2.getId());
        userService.addFriend(friend2, user.getId());
        userService.addFriend(friend2, friend.getId());
        userService.addFriend(friend, friend2.getId());
        List<User> friends = userService.findMutualFriends(user, friend);
        assertEquals(friends.get(0), friend2);
    }
}
