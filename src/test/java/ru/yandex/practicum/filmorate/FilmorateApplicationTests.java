package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.DAO.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.DAO.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.DAO.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.DAO.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;
    private final UserService userService;
    private final FilmDbStorage filmDbStorage;
    private final FilmService filmService;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;


    //UserController
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

    //FilmController
    public Film createFilm() {
        Film film = new Film();
        film.setName("Титаник");
        film.setDescription("Мелодрамма");
        film.setDuration(60);
        film.setReleaseDate(LocalDate.of(1999, 2, 28));
        MpaModel m = new MpaModel();
        m.setId(1);
        m.setName("G");
        film.setGenres(new ArrayList<>());
        film.setMpa(m);
        return filmDbStorage.create(film);
    }

    @Test
    public void testCreateFilm() {
        Film film = new Film();
        film.setName("Титаник");
        film.setDescription("Мелодрамма");
        film.setDuration(60);
        film.setReleaseDate(LocalDate.of(1999, 2, 28));
        MpaModel m = new MpaModel();
        m.setId(1);
        m.setName("G");
        film.setGenres(new ArrayList<>());
        film.setMpa(m);
        film = filmDbStorage.create(film);
        assertEquals(film, filmDbStorage.findFilmById(film.getId()));
    }

    @Test
    public void testUpdateFilm() {
        Film film = createFilm();
        film.setDuration(120);
        filmDbStorage.update(film);
        assertEquals(filmDbStorage.findFilmById(film.getId()).getDuration(), 120);
    }

    @Test
    public void testFindFilmById() {
        createFilm();
        Film film = filmDbStorage.findFilmById(1);
        assertEquals(film.getId(), 1);
    }

    @Test
    public void testFindAllFilms() {
        createFilm();
        createFilm();
        assertNotEquals(filmDbStorage.findAll().size(), 1);
    }

    @Test
    public void testAddLike() {
        Film film = createFilm();
        User user = createUser();
        filmService.addLike(user.getId(), film);
        assertEquals(film.getLikes().size(), 1);
        assertEquals(film.getLikes(), Set.of(user.getId()));
    }

    @Test
    public void testDeleteLike() {
        Film film = createFilm();
        User user = createUser();
        filmService.addLike(user.getId(), film);
        filmService.deleteLike(user.getId(), film);
        assertEquals(film.getLikes().size(), 0);
    }

    @Test
    public void testFindMostPopularFilm() {
        Film film = createFilm();
        Film film2 = createFilm();
        User user = createUser();
        User user2 = createUser();
        User user3 = createUser();
        filmService.addLike(user.getId(), film);
        filmService.addLike(user2.getId(), film);
        filmService.addLike(user3.getId(), film);
        filmService.addLike(user.getId(), film2);
        filmService.addLike(user2.getId(), film2);
        List<Film> mostPopularFilm = filmService.findTop10Films(10);
        assertNotEquals(mostPopularFilm.size(), 0);
        assertNotEquals(mostPopularFilm.size(), 1);
        assertEquals(mostPopularFilm.get(0), film);
        assertEquals(mostPopularFilm.get(1), film2);
    }

    //GenreController

    @Test
    public void testGetAllGenres() {
        assertEquals(genreDbStorage.findAllGenre().size(), 6);
    }

    @Test
    public void testGetGenresById() {
        GenreModel genre = new GenreModel();
        genre.setId(2);
        genre.setName("Драма");
        assertEquals(genre, genreDbStorage.getGenresById(2));
    }

    //MpaController

    @Test
    public void testGetRatingById() {
        MpaModel rating = new MpaModel();
        rating.setId(3);
        rating.setName("PG-13");
        assertEquals(rating, mpaDbStorage.getMpaModel(3));
    }

    @Test
    public void testGetAllRating() {
        assertEquals(mpaDbStorage.findAllMPA().size(), 5);
    }
}
