package ru.SberPo666.metric_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final MetricService metricService;

    @KafkaListener(topics = "${metrics.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "batchKafkaListenerContainerFactory")
    public void listenTrackMetricsBatch(@Payload List<TrackMetricEvent> events) {
        if (events == null || events.isEmpty()) {
            log.trace("Received an empty batch of events.");
            return;
        }
        log.info("Received batch of {} track metric events.", events.size());
        try {
            metricService.processAndStoreMetricsBatch(events);
        } catch (Exception e) {
            log.error("Critical error processing batch of {} metric events. First event trackId: {}. Error: {}",
                    events.size(),
                    events.get(0).getTrackId(),
                    e.getMessage(), e);
        }
    }
}
