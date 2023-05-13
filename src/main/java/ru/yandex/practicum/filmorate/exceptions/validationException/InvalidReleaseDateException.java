package ru.yandex.practicum.filmorate.exceptions.validationException;

public class InvalidReleaseDateException extends RuntimeException {
    public InvalidReleaseDateException(String message) {
        super(message);
    }
}
