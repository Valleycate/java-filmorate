package ru.yandex.practicum.filmorate.exceptions.validationException;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
