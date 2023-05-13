package ru.yandex.practicum.filmorate.exceptions.validationException;

public class InvalidLoginException extends RuntimeException {
    public InvalidLoginException(String message) {
        super(message);
    }
}
