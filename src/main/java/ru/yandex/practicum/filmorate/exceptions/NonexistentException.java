package ru.yandex.practicum.filmorate.exceptions;

public class NonexistentException extends RuntimeException {
    public NonexistentException(String message) {
        super(message);
    }

    public NonexistentException() {
        super();
    }

}
