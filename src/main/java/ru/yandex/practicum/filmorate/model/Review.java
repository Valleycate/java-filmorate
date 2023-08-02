package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class Review {
    private static final int MAX_LENGTH_CONTENT = 2000;
    private Long reviewId;
    @NotNull
    private Integer userId;
    @NotNull
    private Integer filmId;
    @Size(max = MAX_LENGTH_CONTENT, message = "content is more than 2000 symbols")
    @NotNull
    private String content;
    @NotNull
    private Boolean isPositive; //положительный или отрицательный отзыв
    private Integer useful; //количество лайков минус количество дизлайков
}
