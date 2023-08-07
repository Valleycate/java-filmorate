package ru.yandex.practicum.filmorate.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.enums.EnumEventType;
import ru.yandex.practicum.filmorate.model.enums.EnumOperation;
import ru.yandex.practicum.filmorate.storage.DAO.storage.FeedDbStorage;

import java.time.Instant;

@Component
public final class FeedSaver {
    private static FeedDbStorage feedStorage;

    @Autowired
    private FeedSaver(FeedDbStorage feedStorage) {
        FeedSaver.feedStorage = feedStorage;
    }

    public static void saveFeed(Integer userId, Long entityId, EnumEventType eventType, EnumOperation operation) {
        feedStorage.save(Feed.builder()
                .userId(userId)
                .entityId(entityId)
                .eventType(eventType)
                .operation(operation)
                .timestamp(Instant.now().toEpochMilli())
                .build());
    }
}
