package ru.yandex.practicum.filmorate.exceptions.validationException;

public class InvalidDescriptionException extends RuntimeException {
    public InvalidDescriptionException(String message) {
        super(message);
    }
}