package ru.yandex.practicum.filmorate.exceptions.validationException;

public class InvalidIdException extends RuntimeException {
    public InvalidIdException(String message) {
        super(message);
    }
}
