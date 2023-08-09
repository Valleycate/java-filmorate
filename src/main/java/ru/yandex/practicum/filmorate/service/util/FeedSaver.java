package ru.yandex.practicum.filmorate.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EnumEventType;
import ru.yandex.practicum.filmorate.model.enums.EnumOperation;
import ru.yandex.practicum.filmorate.storage.DAO.storage.FeedDbStorage;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public final class FeedSaver {
    private final FeedDbStorage feedStorage;

    public void saveFeed(Integer userId, Long entityId, EnumEventType eventType, EnumOperation operation) {
        feedStorage.save(Feed.builder()
                .userId(userId)
                .entityId(entityId)
                .eventType(eventType)
                .operation(operation)
                .timestamp(Instant.now().toEpochMilli())
                .build());
    }
}
