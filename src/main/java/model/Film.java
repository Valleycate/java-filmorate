package model;

import lombok.Data;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Setter
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDateTime releaseDate;
    private Duration duration;
}
