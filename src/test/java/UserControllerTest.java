import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerTest {
    private InMemoryUserStorage userStorage = new InMemoryUserStorage();

    @Test
    public void shouldCreateUser() {
        userStorage = new InMemoryUserStorage();
        if (userStorage.findAll().size() != 0) {
            userStorage.findAll().clear();
        }
        try {
            User user = null;
            userStorage.create(user);
        } catch (RuntimeException ignored) {
        }
        assertEquals(0, userStorage.findAll().size());

        try {
            User user = new User();
            userStorage.create(user);
        } catch (RuntimeException ignored) {
        }
        assertEquals(0, userStorage.findAll().size());

        try {
            User user = new User();
            user.setLogin(" ");
            userStorage.create(user);
        } catch (RuntimeException e) {
            assertEquals("Неверный логин", e.getMessage());
        }
        assertEquals(0, userStorage.findAll().size());

        try {
            User user = new User();
            user.setLogin("Anim");
            user.setEmail("Email.com");
            userStorage.create(user);
        } catch (RuntimeException e) {
            assertEquals("Неверный адрес электронной почты", e.getMessage());
        }
        assertEquals(0, userStorage.findAll().size());

        try {
            User user = new User();
            user.setLogin("Anim");
            user.setEmail("Email.com@Anim");
            user.setName("Маша");
            user.setBirthday(LocalDate.of(2222, 2, 28));
            userStorage.create(user);
        } catch (RuntimeException e) {
            assertEquals("Дата рождения не может быть в будущем", e.getMessage());
        }
        assertEquals(0, userStorage.findAll().size());

        try {
            User user = new User();
            user.setLogin("Anim");
            user.setEmail("Email.com@Anim");
            user.setName("Маша");
            user.setBirthday(LocalDate.of(2000, 2, 28));
            userStorage.create(user);
        } catch (RuntimeException ignored) {
        }
        assertEquals(1, userStorage.findAll().size());

        User user = new User();
        user.setLogin("some_login");
        user.setEmail("Email.com@Someone");
        user.setName("some_name");
        user.setBirthday(LocalDate.of(2000, 2, 28));
        userStorage.create(user);
        assertEquals("some_name", user.getName());

        assertEquals(2, userStorage.findAll().size());
    }

    @Test
    public void shouldUpdateUser() {
        User newUser = new User();
        newUser.setLogin("Anim");
        newUser.setEmail("Email.com@Anim");
        newUser.setName("Маша");
        newUser.setBirthday(LocalDate.of(2000, 2, 28));
        userStorage.create(newUser);
        try {
            User user = null;
            userStorage.update(user);
        } catch (RuntimeException e) {
            assertEquals("Такого пользователя нет!", e.getMessage());
        }
        assertEquals(1, userStorage.findAll().size());

        try {
            User user = new User();
            userStorage.update(user);
        } catch (RuntimeException e) {
            assertEquals("Такого пользователя нет!", e.getMessage());
        }
        assertEquals(1, userStorage.findAll().size());

        try {
            User user = new User();
            user.setId(newUser.getId());
            user.setLogin(" ");
            userStorage.update(user);
        } catch (RuntimeException e) {
            assertEquals("Неверный логин", e.getMessage());
        }
        assertEquals(1, userStorage.findAll().size());

        try {
            User user = new User();
            user.setId(newUser.getId());
            user.setLogin("Anim");
            user.setEmail("Email.com");
            userStorage.update(user);
        } catch (RuntimeException e) {
            assertEquals("Неверный адрес электронной почты", e.getMessage());
        }
        assertEquals(1, userStorage.findAll().size());

        try {
            User user = new User();
            user.setId(newUser.getId());
            user.setLogin("Anim");
            user.setEmail("Email.com@Anim");
            user.setName("Маша");
            user.setBirthday(LocalDate.of(2222, 2, 28));
            userStorage.update(user);
        } catch (RuntimeException e) {
            assertEquals("Дата рождения не может быть в будущем", e.getMessage());
        }
        assertEquals(1, userStorage.findAll().size());

        try {
            User user = new User();
            user.setId(newUser.getId());
            user.setLogin("Anim");
            user.setEmail("Email.com@Anim");
            user.setName("Маша");
            user.setBirthday(LocalDate.of(2000, 2, 28));
            userStorage.update(user);
        } catch (RuntimeException ignored) {
        }
        assertEquals(1, userStorage.findAll().size());

        User user = new User();
        user.setLogin("Anim");
        user.setEmail("Email.com@Anim");
        user.setName("");
        user.setBirthday(LocalDate.of(2000, 2, 28));
        userStorage.create(user);
        userStorage.update(user);
        assertEquals("Anim", user.getName());

        assertEquals(2, userStorage.findAll().size());
    }

}
