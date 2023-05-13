package ru.yandex.practicum.filmorate.exceptions.validationException;

public class InvalidDurationException extends RuntimeException {
    public InvalidDurationException(String message) {
        super(message);
    }
}