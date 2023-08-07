package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.MpaModel;
import ru.yandex.practicum.filmorate.storage.DAO.storage.MpaDbStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaControllerTest {
    private final MpaDbStorage mpaDbStorage;

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
