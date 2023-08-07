package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.enums.EnumEventType;
import ru.yandex.practicum.filmorate.model.enums.EnumOperation;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Feed {
    private Long eventId;
    private Integer userId;
    private Long entityId;
    private EnumEventType eventType;
    private EnumOperation operation;
    private Long timestamp;
}
