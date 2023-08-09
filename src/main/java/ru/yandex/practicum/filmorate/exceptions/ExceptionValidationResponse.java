package ru.yandex.practicum.filmorate.exceptions;

import lombok.Getter;

import java.util.Map;

@Getter
public class ExceptionValidationResponse extends ExceptionResponse {
    private final Map<String, String> errors;

    public ExceptionValidationResponse(Map<String, String> errors) {
        super("Ошибка валидации");
        this.errors = errors;
    }
}
