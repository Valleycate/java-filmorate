import controller.UserController;
import model.User;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerTest {
    UserController userController = new UserController();

    @Test
    public void shouldCreateUser(){
        try {
            User user = null;
            userController.create(user);
        }catch (RuntimeException e){
        }
        assertEquals(0, userController.findAll().size());

        try{
            User user = new User();
            userController.create(user);
        }catch (RuntimeException e){
        }
        assertEquals(0, userController.findAll().size());

        try{
            User user = new User();
            user.setLogin(" ");
            userController.create(user);
        }catch (RuntimeException e){
            assertEquals("Неверный логин", e.getMessage());
        }
        assertEquals(0, userController.findAll().size());

        try{
            User user = new User();
            user.setLogin("Anim");
            user.setEmail("Email.com");
            userController.create(user);
        }catch (RuntimeException e){
            assertEquals("Неверный адрес электронной почты", e.getMessage());
        }
        assertEquals(0, userController.findAll().size());

        try{
            User user = new User();
            user.setLogin("Anim");
            user.setEmail("Email.com@Anim");
            user.setName("Маша");
            user.setBirthday(LocalDateTime.of(2222,2,28,3,55));
            userController.create(user);
        }catch (RuntimeException e){
            assertEquals("Дата рождения не может быть в будущем", e.getMessage());
        }
        assertEquals(0, userController.findAll().size());

        try{
            User user = new User();
            user.setLogin("Anim");
            user.setEmail("Email.com@Anim");
            user.setName("Маша");
            user.setBirthday(LocalDateTime.of(2000,2,28,3,55));
            userController.create(user);
        }catch (RuntimeException e){
        }
        assertEquals(1, userController.findAll().size());

        User user = new User();
        user.setLogin("Anim");
        user.setEmail("Email.com@Anim");
        user.setName("");
        user.setBirthday(LocalDateTime.of(2000,2,28,3,55));
        userController.create(user);
        assertEquals("Anim", user.getName());

        assertEquals(2, userController.findAll().size());
    }
    @Test
    public void shouldUpdateUser() {
        try {
            User user = null;
            userController.update(user);
        } catch (RuntimeException e) {
        }
        assertEquals(0, userController.findAll().size());

        try {
            User user = new User();
            userController.update(user);
        } catch (RuntimeException e) {
        }
        assertEquals(0, userController.findAll().size());

        try {
            User user = new User();
            user.setLogin(" ");
            userController.update(user);
        } catch (RuntimeException e) {
            assertEquals("Неверный логин", e.getMessage());
        }
        assertEquals(0, userController.findAll().size());

        try {
            User user = new User();
            user.setLogin("Anim");
            user.setEmail("Email.com");
            userController.update(user);
        } catch (RuntimeException e) {
            assertEquals("Неверный адрес электронной почты", e.getMessage());
        }
        assertEquals(0, userController.findAll().size());

        try {
            User user = new User();
            user.setLogin("Anim");
            user.setEmail("Email.com@Anim");
            user.setName("Маша");
            user.setBirthday(LocalDateTime.of(2222, 2, 28, 3, 55));
            userController.update(user);
        } catch (RuntimeException e) {
            assertEquals("Дата рождения не может быть в будущем", e.getMessage());
        }
        assertEquals(0, userController.findAll().size());

        try {
            User user = new User();
            user.setLogin("Anim");
            user.setEmail("Email.com@Anim");
            user.setName("Маша");
            user.setBirthday(LocalDateTime.of(2000, 2, 28, 3, 55));
            userController.update(user);
        } catch (RuntimeException e) {
        }
        assertEquals(1, userController.findAll().size());

        User user = new User();
        user.setLogin("Anim");
        user.setEmail("Email.com@Anim");
        user.setName("");
        user.setBirthday(LocalDateTime.of(2000,2,28,3,55));
        userController.update(user);
        assertEquals("Anim", user.getName());

        assertEquals(2, userController.findAll().size());
    }

}
