package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NotNull
    @NotBlank
    private String name;
    @Max(value = 200)
    @NotNull
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    @Min(value = 0)
    private Integer duration;
}
