package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class User {
    @EqualsAndHashCode.Exclude
    private int id;
    @Pattern(regexp = "\\S+", message = "login with whitespaces")
    @NotBlank(message = "Please provide a login")
    private String login;
    private String name;
    @NotEmpty
    @Email(message = "Please provide a email")
    private String email;
    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();
    private Map<Integer, Friendship> friendship;

    public void validateName() {
        if (name == null || name.isEmpty() || name.isBlank()) {
            name = login;
        }
    }

}
