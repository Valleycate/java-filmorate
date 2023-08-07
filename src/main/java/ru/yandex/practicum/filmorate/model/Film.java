package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    @Size(max = 200)
    private String description;
    @NotNull
    @ReleaseDate
    private LocalDate releaseDate;
    @NotNull
    @PositiveOrZero
    private Integer duration;
    private Set<Integer> likes = new HashSet<>();
    private ArrayList<GenreModel> genres;
    private MpaModel mpa;
    private List<Director> directors;
}
