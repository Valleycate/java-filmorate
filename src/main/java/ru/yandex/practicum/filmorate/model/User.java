package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class User {
    private int id;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String login;
    //@BlankName(login)
    private String name;//была ещё попытка name = login,
    // но из-за того что передают "" вместо name,
    // такой вариант не работает
    @NotNull
    @PastOrPresent
    private LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();
    private Map<Integer, Friendship> friendship;

    public void validateName() {
        if (name == null || name.isEmpty() || name.isBlank()) {
            name = login;
        }
    }

}
