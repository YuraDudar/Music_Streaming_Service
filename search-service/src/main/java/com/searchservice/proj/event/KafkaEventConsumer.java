package com.searchservice.proj.event;

import com.searchservice.proj.event.catalog.track.TrackCreatedEvent;
import com.searchservice.proj.event.catalog.track.TrackUpdatedEvent;
import com.searchservice.proj.event.catalog.CatalogEvent;

import com.searchservice.proj.event.catalog.album.AlbumCreatedEvent;
import com.searchservice.proj.event.catalog.album.AlbumDeletedEvent;
import com.searchservice.proj.event.catalog.album.AlbumUpdatedEvent;
import com.searchservice.proj.event.catalog.artist.ArtistCreatedEvent;
import com.searchservice.proj.event.catalog.artist.ArtistDeletedEvent;
import com.searchservice.proj.event.catalog.artist.ArtistUpdatedEvent;
import com.searchservice.proj.event.catalog.track.TrackDeletedEvent;

import com.searchservice.proj.service.IndexingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventConsumer {

    private final IndexingService indexingService;

    @KafkaListener(
            topics = "${app.kafka.topic.tracks}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenTrackEvents(@Payload CatalogEvent<?> event,
                                  @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                  @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Received event type: {} from topic: {}, key: {}, eventId: {}",
                event.eventType(), topic, key, event.eventId());

        try {
            Object payloadObject = event.payload(); 

            if ("TrackCreated".equals(event.eventType())) {
                if (payloadObject instanceof TrackCreatedEvent payload) { 
                    indexingService.indexTrack(payload);
                } else {
                    log.warn("Payload for TrackCreated is not of expected type: {}", payloadObject.getClass().getName());
                }
            } else if ("TrackUpdated".equals(event.eventType())) {
                if (payloadObject instanceof TrackUpdatedEvent payload) {
                    indexingService.updateTrack(payload);
                } else {
                    log.warn("Payload for TrackUpdated is not of expected type: {}", payloadObject.getClass().getName());
                }
            } else if ("TrackDeleted".equals(event.eventType())) {
                if (payloadObject instanceof TrackDeletedEvent payload) {
                    indexingService.deleteTrack(payload.trackId());
                } else {
                    log.warn("Payload for TrackDeleted is not of expected type: {}", payloadObject.getClass().getName());
                }
            } else {
                log.warn("Received unhandled track event type: {} for aggregateId: {}", event.eventType(), event.aggregateId());
            }
        } catch (Exception e) {
            log.error("Error processing track event [ID: {}, Type: {}]: {}", event.eventId(), event.eventType(), e.getMessage(), e);
            
        }
    }

    @KafkaListener(
            topics = "${app.kafka.topic.albums}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenAlbumEvents(@Payload CatalogEvent<?> event,
                                  @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                  @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Received event type: {} from topic: {}, key: {}, eventId: {}",
                event.eventType(), topic, key, event.eventId());
        try {
            Object payloadObject = event.payload();
            if ("AlbumCreated".equals(event.eventType())) {
                if (payloadObject instanceof AlbumCreatedEvent payload) {
                    indexingService.indexAlbum(payload);
                } else {
                    log.warn("Payload for AlbumCreated is not of expected type: {}", payloadObject.getClass().getName());
                }
            } else if ("AlbumUpdated".equals(event.eventType())) {
                if (payloadObject instanceof AlbumUpdatedEvent payload) {
                    indexingService.updateAlbum(payload);
                } else {
                    log.warn("Payload for AlbumUpdated is not of expected type: {}", payloadObject.getClass().getName());
                }
            } else if ("AlbumDeleted".equals(event.eventType())) {
                if (payloadObject instanceof AlbumDeletedEvent payload) {
                    indexingService.deleteAlbum(payload.albumId());
                } else {
                    log.warn("Payload for AlbumDeleted is not of expected type: {}", payloadObject.getClass().getName());
                }
            } else {
                log.warn("Received unhandled album event type: {} for aggregateId: {}", event.eventType(), event.aggregateId());
            }
        } catch (Exception e) {
            log.error("Error processing album event [ID: {}, Type: {}]: {}", event.eventId(), event.eventType(), e.getMessage(), e);
        }
    }

    @KafkaListener(
            topics = "${app.kafka.topic.artists}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenArtistEvents(@Payload CatalogEvent<?> event,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                   @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Received event type: {} from topic: {}, key: {}, eventId: {}",
                event.eventType(), topic, key, event.eventId());
        try {
            Object payloadObject = event.payload();
            if ("ArtistCreated".equals(event.eventType())) {
                if (payloadObject instanceof ArtistCreatedEvent payload) {
                    indexingService.indexArtist(payload);
                } else {
                    log.warn("Payload for ArtistCreated is not of expected type: {}", payloadObject.getClass().getName());
                }
            } else if ("ArtistUpdated".equals(event.eventType())) {
                if (payloadObject instanceof ArtistUpdatedEvent payload) {
                    indexingService.updateArtist(payload);
                } else {
                    log.warn("Payload for ArtistUpdated is not of expected type: {}", payloadObject.getClass().getName());
                }
            } else if ("ArtistDeleted".equals(event.eventType())) {
                if (payloadObject instanceof ArtistDeletedEvent payload) {
                    indexingService.deleteArtist(payload.artistId());
                } else {
                    log.warn("Payload for ArtistDeleted is not of expected type: {}", payloadObject.getClass().getName());
                }
            } else {
                log.warn("Received unhandled artist event type: {} for aggregateId: {}", event.eventType(), event.aggregateId());
            }
        } catch (Exception e) {
            log.error("Error processing artist event [ID: {}, Type: {}]: {}", event.eventId(), event.eventType(), e.getMessage(), e);
        }
    }
}