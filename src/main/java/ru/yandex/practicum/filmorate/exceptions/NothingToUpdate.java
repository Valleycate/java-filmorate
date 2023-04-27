package ru.yandex.practicum.filmorate.exceptions;

public class NothingToUpdate extends RuntimeException {
    public NothingToUpdate(String message) {
        super(message);
    }

}
