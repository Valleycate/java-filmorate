package ru.yandex.practicum.filmorate.model;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = ReleaseDateValidator.class)
public @interface ReleaseDate {

    String message() default "{ReleaseDate.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
