package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.validationException.*;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@RestController
public class BasicErrorHandler {

    @ExceptionHandler({InvalidIdException.class, InvalidBirthdayException.class, InvalidDescriptionException.class,
            InvalidDurationException.class, InvalidEmailException.class, InvalidLoginException.class,
            InvalidNameException.class, InvalidReleaseDateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNonexistentException(final RuntimeException e) {
        return Map.of("Non-existent object", e.getMessage());
    }

    @ExceptionHandler({BadRequest.class, NonexistentException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleValidationException(final RuntimeException e) {
        return Map.of("bad validation", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ExceptionValidationResponse(errors);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleException(final ConstraintViolationException e) {
        return new ExceptionResponse("Некорректные параметры запроса");
    }

}
