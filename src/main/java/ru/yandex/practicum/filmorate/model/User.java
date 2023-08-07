package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;


@Data
@NoArgsConstructor
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

    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.birthday = birthday;
        this.name = name;
    }

}
