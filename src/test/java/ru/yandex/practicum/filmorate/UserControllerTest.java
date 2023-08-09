package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.DAO.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.List;

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
        user.setBirthday(LocalDate.of(2000, 2, 28));
        return userStorage.create(user);
    }

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setLogin("some_login");
        user.setEmail("Email.com@Someone");
        user.setName("some_name");
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
        userService.addFriend(user, friend);
        assertEquals(userService.getFriends(user.getId()).size(), 1);
        assertEquals(userService.getFriends(friend.getId()).size(), 0);
        userService.addFriend(friend, user);
        assertEquals(userService.getFriends(user.getId()).size(), 1);
        assertEquals(userService.getFriends(friend.getId()).size(), 1);
    }

    @Test
    public void testDeleteFriend() {
        User user = createUser();
        User friend = createUser();
        userService.addFriend(user, friend);
        userService.addFriend(friend, user);
        userService.deleteFriend(user, friend);
        assertEquals(userService.getFriends(user.getId()).size(), 0);
        assertEquals(userService.getFriends(friend.getId()).size(), 1);
    }

    @Test
    public void testFindAllFriends() {
        User user = createUser();
        User friend = createUser();
        User friend2 = createUser();
        userService.addFriend(user, friend);
        userService.addFriend(friend, user);
        userService.addFriend(user, friend2);
        userService.addFriend(friend2, user);
        List<User> friends = userService.getFriends(user.getId());
        assertEquals(friends.get(0), friend);
        assertEquals(friends.get(1), friend2);
    }

    @Test
    public void testFindMutualFriends() {
        User user = createUser();
        User friend = createUser();
        User friend2 = createUser();
        userService.addFriend(user, friend);
        userService.addFriend(friend, user);
        userService.addFriend(user, friend2);
        userService.addFriend(friend2, user);
        userService.addFriend(friend2, friend);
        userService.addFriend(friend, friend2);
        List<User> friends = userService.findMutualFriends(user.getId(), friend.getId());
        assertEquals(friends.get(0), friend2);
    }
}
