package ru.yandex.practicum.filmorate.exceptions.validationException;

public class InvalidNameException extends RuntimeException {
    public InvalidNameException(String message) {
        super(message);
    }
}
