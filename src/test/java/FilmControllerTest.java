import controller.FilmController;
import model.Film;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTest {
    FilmController filmController = new FilmController();

    @Test
    public void shouldCreateFilm(){
        try {
            Film film = null;
            filmController.create(film);
        }catch (RuntimeException e){
        }
        assertEquals(0, filmController.findAll().size());

        try{
            Film film = new Film();
            filmController.create(film);
        }catch (RuntimeException e){
        }
        assertEquals(0, filmController.findAll().size());

        try{
            Film film = new Film();
            film.setName(" ");
            filmController.create(film);
        }catch (RuntimeException e){
            assertEquals("Неверное имя", e.getMessage());
        }
        assertEquals(0, filmController.findAll().size());

        try{
            Film film = new Film();
            film.setName("Титаник");
            film.setDescription("0123456789"+"0123456789"+"0123456789"+"0123456789"+"0123456789"+"0123456789"+"0123456789"
                    +"0123456789"+"0123456789"+"0123456789"+"0123456789"+"0123456789"+"0123456789"+"0123456789"+"0123456789"
                    +"0123456789"+"0123456789"+"0123456789"+"0123456789"+"0123456789" + "0");
            filmController.create(film);
        }catch (RuntimeException e){
            assertEquals("Описание слишком большое", e.getMessage());
        }
        assertEquals(0, filmController.findAll().size());

        try{
            Film film = new Film();
            film.setName("Титаник");
            film.setDescription("Мелодрамма");
            film.setDuration(Duration.ofMinutes(-10));
            filmController.create(film);
        }catch (RuntimeException e){
            assertEquals("Продолжительность фильма должна быть положительной", e.getMessage());
        }
        assertEquals(0, filmController.findAll().size());

        try{
            Film film = new Film();
            film.setName("Титаник");
            film.setDescription("Мелодрамма");
            film.setDuration(Duration.ofMinutes(60));
            film.setReleaseDate(LocalDateTime.of(1894,2,28,3,55));
            filmController.create(film);
        }catch (RuntimeException e){
            assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", e.getMessage());
        }
        assertEquals(0, filmController.findAll().size());

        try{
            Film film = new Film();
            film.setName("Титаник");
            film.setDescription("Мелодрамма");
            film.setDuration(Duration.ofMinutes(60));
            film.setReleaseDate(LocalDateTime.of(1999,2,28,3,55));
            filmController.create(film);
        }catch (RuntimeException e){
        }
        assertEquals(1, filmController.findAll().size());
    }
    @Test
    public void shouldUpdateFilm(){
        try {
            Film film = null;
            filmController.update(film);
        }catch (RuntimeException e){
        }
        assertEquals(0, filmController.findAll().size());

        try{
            Film film = new Film();
            filmController.update(film);
        }catch (RuntimeException e){
        }
        assertEquals(0, filmController.findAll().size());

        try{
            Film film = new Film();
            film.setName(" ");
            filmController.update(film);
        }catch (RuntimeException e){
            assertEquals("Неверное имя", e.getMessage());
        }
        assertEquals(0, filmController.findAll().size());

        try{
            Film film = new Film();
            film.setName("Титаник");
            film.setDescription("0123456789"+"0123456789"+"0123456789"+"0123456789"+"0123456789"+"0123456789"+"0123456789"
                    +"0123456789"+"0123456789"+"0123456789"+"0123456789"+"0123456789"+"0123456789"+"0123456789"+"0123456789"
                    +"0123456789"+"0123456789"+"0123456789"+"0123456789"+"0123456789" + "0");
            filmController.update(film);
        }catch (RuntimeException e){
            assertEquals("Описание слишком большое", e.getMessage());
        }
        assertEquals(0, filmController.findAll().size());

        try{
            Film film = new Film();
            film.setName("Титаник");
            film.setDescription("Мелодрамма");
            film.setDuration(Duration.ofMinutes(-10));
            filmController.update(film);
        }catch (RuntimeException e){
            assertEquals("Продолжительность фильма должна быть положительной", e.getMessage());
        }
        assertEquals(0, filmController.findAll().size());

        try{
            Film film = new Film();
            film.setName("Титаник");
            film.setDescription("Мелодрамма");
            film.setDuration(Duration.ofMinutes(60));
            film.setReleaseDate(LocalDateTime.of(1894,2,28,3,55));
            filmController.update(film);
        }catch (RuntimeException e){
            assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", e.getMessage());
        }
        assertEquals(0, filmController.findAll().size());

        try{
            Film film = new Film();
            film.setName("Титаник");
            film.setDescription("Мелодрамма");
            film.setDuration(Duration.ofMinutes(60));
            film.setReleaseDate(LocalDateTime.of(1999,2,28,3,55));
            filmController.update(film);
        }catch (RuntimeException e){
        }
        assertEquals(1, filmController.findAll().size());
    }

}
