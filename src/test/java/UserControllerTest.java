import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerTest {
    UserController userController = new UserController(new UserService(new InMemoryUserStorage()));

    @Test
    public void shouldCreateUser() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
        if (userController.findAll().size() != 0){
            userController.findAll().clear();
        }
        try {
            User user = null;
            userController.create(user);
        } catch (RuntimeException ignored) {
        }
        assertEquals(0, userController.findAll().size());

        try {
            User user = new User();
            userController.create(user);
        } catch (RuntimeException ignored) {
        }
        assertEquals(0, userController.findAll().size());

        try {
            User user = new User();
            user.setLogin(" ");
            userController.create(user);
        } catch (RuntimeException e) {
            assertEquals("Неверный логин", e.getMessage());
        }
        assertEquals(0, userController.findAll().size());

        try {
            User user = new User();
            user.setLogin("Anim");
            user.setEmail("Email.com");
            userController.create(user);
        } catch (RuntimeException e) {
            assertEquals("Неверный адрес электронной почты", e.getMessage());
        }
        assertEquals(0, userController.findAll().size());

        try {
            User user = new User();
            user.setLogin("Anim");
            user.setEmail("Email.com@Anim");
            user.setName("Маша");
            user.setBirthday(LocalDate.of(2222, 2, 28));
            userController.create(user);
        } catch (RuntimeException e) {
            assertEquals("Дата рождения не может быть в будущем", e.getMessage());
        }
        assertEquals(0, userController.findAll().size());

        try {
            User user = new User();
            user.setLogin("Anim");
            user.setEmail("Email.com@Anim");
            user.setName("Маша");
            user.setBirthday(LocalDate.of(2000, 2, 28));
            userController.create(user);
        } catch (RuntimeException ignored) {
        }
        assertEquals(1, userController.findAll().size());

        User user = new User();
        user.setLogin("Anim");
        user.setEmail("Email.com@Anim");
        user.setName("");
        user.setBirthday(LocalDate.of(2000, 2, 28));
        userController.create(user);
        assertEquals("Anim", user.getName());

        assertEquals(2, userController.findAll().size());
    }

    @Test
    public void shouldUpdateUser() {
        User newUser = new User();
        newUser.setLogin("Anim");
        newUser.setEmail("Email.com@Anim");
        newUser.setName("Маша");
        newUser.setBirthday(LocalDate.of(2000, 2, 28));
        userController.create(newUser);
        try {
            User user = null;
            userController.update(user);
        } catch (RuntimeException e) {
            assertEquals("Такого пользователя нет!", e.getMessage());
        }
        assertEquals(1, userController.findAll().size());

        try {
            User user = new User();
            userController.update(user);
        } catch (RuntimeException e) {
            assertEquals("Такого пользователя нет!", e.getMessage());
        }
        assertEquals(1, userController.findAll().size());

        try {
            User user = new User();
            user.setId(newUser.getId());
            user.setLogin(" ");
            userController.update(user);
        } catch (RuntimeException e) {
            assertEquals("Неверный логин", e.getMessage());
        }
        assertEquals(1, userController.findAll().size());

        try {
            User user = new User();
            user.setId(newUser.getId());
            user.setLogin("Anim");
            user.setEmail("Email.com");
            userController.update(user);
        } catch (RuntimeException e) {
            assertEquals("Неверный адрес электронной почты", e.getMessage());
        }
        assertEquals(1, userController.findAll().size());

        try {
            User user = new User();
            user.setId(newUser.getId());
            user.setLogin("Anim");
            user.setEmail("Email.com@Anim");
            user.setName("Маша");
            user.setBirthday(LocalDate.of(2222, 2, 28));
            userController.update(user);
        } catch (RuntimeException e) {
            assertEquals("Дата рождения не может быть в будущем", e.getMessage());
        }
        assertEquals(1, userController.findAll().size());

        try {
            User user = new User();
            user.setId(newUser.getId());
            user.setLogin("Anim");
            user.setEmail("Email.com@Anim");
            user.setName("Маша");
            user.setBirthday(LocalDate.of(2000, 2, 28));
            userController.update(user);
        } catch (RuntimeException ignored) {
        }
        assertEquals(1, userController.findAll().size());

        User user = new User();
        user.setLogin("Anim");
        user.setEmail("Email.com@Anim");
        user.setName("");
        user.setBirthday(LocalDate.of(2000, 2, 28));
        userController.create(user);
        userController.update(user);
        assertEquals("Anim", user.getName());

        assertEquals(2, userController.findAll().size());
    }

}
