package ru.yandex.practicum.filmorate.exceptions;

public class InvalidBirthdayException extends RuntimeException {
    public InvalidBirthdayException(String message) {
        super(message);
    }
}
