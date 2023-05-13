package ru.yandex.practicum.filmorate.exceptions.validationException;

public class InvalidBirthdayException extends RuntimeException {
    public InvalidBirthdayException(String message) {
        super(message);
    }
}
