package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.validationException.InvalidNameException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DAO.storage.DirectorDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorDbStorage directorDbStorage;

    private void checkName(Director director) {
        if (director.getName() == null || director.getName().isBlank()) {
            throw new InvalidNameException("Имя некорректное");
        }
    }

    public List<Director> getAllDirectors() {
        return directorDbStorage.getDirectors();
    }

    public Director getDirectorById(int id) {
        return directorDbStorage.getDirector(id);
    }

    public Director addDirector(Director director) {
        checkName(director);
        return directorDbStorage.add(director);
    }

    public Director updateDirector(Director director) {
        checkName(director);
        return directorDbStorage.update(director);
    }

    public void deleteDirector(int id) {
        directorDbStorage.delete(id);
    }
}
