package ru.SberPo666.stream_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendListeningMetric(UUID trackId) {
        kafkaTemplate.send("listening-metrics", trackId.toString());
    }

    public void sendHistory(UUID trackId, UUID userId) throws JsonProcessingException {
        Map<String, String> map = new HashMap<>();
        map.put("trackId", trackId.toString());
        map.put("userId", userId.toString());
        kafkaTemplate.send("history-topic", new ObjectMapper().writeValueAsString(map));
    }
}
