package ru.SberPo666.stream_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TrackEventConsumer {

    private final TrackRepository trackRepository;

    class TrackEvent {
        String trackId;
        String s3key;
        String eventType;
    }

    @KafkaListener(topics = "catalog.track-audio-sources.v1", groupId = "music-streaming")
    public void consumeTrackEvent(String message) throws JsonProcessingException {
        TrackEvent event = new ObjectMapper().readValue(message, TrackEvent.class);

        if(event.eventType.equals("CREATED") || event.eventType.equals("UPDATED")) {
            if (!trackRepository.existsById(UUID.fromString(event.trackId))) {
                Track track = Track.builder()
                        .id(UUID.fromString(event.trackId))
                        .s3key(event.s3key)
                        .build();

                trackRepository.save(track);
            }
        }
        else if(event.eventType.equals("DELETED")){
            trackRepository.deleteById(UUID.fromString(event.trackId));
        }
    }

}
