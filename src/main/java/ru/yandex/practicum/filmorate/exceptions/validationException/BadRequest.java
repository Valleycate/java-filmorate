package ru.yandex.practicum.filmorate.exceptions.validationException;

public class BadRequest extends RuntimeException {
    public BadRequest(String message) {
        super(message);
    }
}
