package ru.SberPo666.interaction_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.SberPo666.interaction_service.Entity.HistoryEntity;
import ru.SberPo666.interaction_service.repository.HistoryRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class HistoryConsumer {

    HistoryRepository historyRepository;

    static class HistoryEvent{
        UUID trackId;
        UUID userId;
    }

    @KafkaListener(topics = "history-events", groupId = "music-streaming")
    public void consumeCreateTrackEvent(String message) throws JsonProcessingException {
        HistoryEvent event = new ObjectMapper().readValue(message, HistoryEvent.class);

        HistoryEntity history = HistoryEntity.builder()
                .track_id(event.trackId)
                .user_id(event.userId)
                .listened_at(LocalDateTime.now())
                .build();

        historyRepository.save(history);
    }

}
