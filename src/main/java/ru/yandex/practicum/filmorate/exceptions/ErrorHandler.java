package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.exceptions.validationException.*;

import java.util.Map;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNonexistentException(final NonexistentException e) {
        return Map.of("Non-existent object", e.getMessage());
    }

    @ExceptionHandler({InvalidIdException.class, InvalidBirthdayException.class, InvalidDescriptionException.class,
            InvalidDurationException.class, InvalidEmailException.class, InvalidLoginException.class,
            InvalidNameException.class, InvalidReleaseDateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final RuntimeException e) {
        return Map.of("bad validation", e.getMessage());
    }

}
