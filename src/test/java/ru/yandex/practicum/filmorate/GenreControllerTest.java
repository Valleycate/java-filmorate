package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.GenreModel;
import ru.yandex.practicum.filmorate.storage.DAO.storage.GenreDbStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreControllerTest {
    private final GenreDbStorage genreDbStorage;

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
}
