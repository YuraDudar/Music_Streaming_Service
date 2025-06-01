package com.example.catalogservice.service.impl;
import com.example.catalogservice.event.CatalogEvent;
import com.example.catalogservice.event.album.AlbumCreatedEvent;
import com.example.catalogservice.event.album.AlbumDeletedEvent;
import com.example.catalogservice.event.album.AlbumUpdatedEvent;
import com.example.catalogservice.event.artist.ArtistCreatedEvent;
import com.example.catalogservice.event.artist.ArtistDeletedEvent;
import com.example.catalogservice.event.artist.ArtistUpdatedEvent;
import com.example.catalogservice.event.genre.GenreCreatedEvent;
import com.example.catalogservice.event.genre.GenreDeletedEvent;
import com.example.catalogservice.event.genre.GenreUpdatedEvent;
import com.example.catalogservice.event.track.TrackCreatedEvent;
import com.example.catalogservice.event.track.TrackDeletedEvent;
import com.example.catalogservice.event.track.TrackUpdatedEvent;
import com.example.catalogservice.service.KafkaEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventProducerImpl implements KafkaEventProducer {

    private final KafkaTemplate<String, CatalogEvent<?>> kafkaTemplate;

    @Value("${app.kafka.topic.tracks}")
    private String tracksTopic;

    @Value("${app.kafka.topic.albums}")
    private String albumsTopic;

    @Value("${app.kafka.topic.artists}")
    private String artistsTopic;

    @Value("${app.kafka.topic.genres}")
    private String genresTopic;

    @Override
    public void sendTrackCreatedEvent(TrackCreatedEvent payload) {
        var event = new CatalogEvent<>(
                UUID.randomUUID(), "TrackCreated", OffsetDateTime.now(),
                payload.trackId().toString(), "Track", 1, payload);
        log.info("Sending TrackCreatedEvent to Kafka. Event ID: {}", event.eventId());
        kafkaTemplate.send(tracksTopic, event.aggregateId(), event);
    }

    @Override
    public void sendTrackUpdatedEvent(TrackUpdatedEvent payload) {
        var event = new CatalogEvent<>(
                UUID.randomUUID(), "TrackUpdated", OffsetDateTime.now(),
                payload.trackId().toString(), "Track", 1, payload);
        log.info("Sending TrackUpdatedEvent to Kafka. Event ID: {}", event.eventId());
        kafkaTemplate.send(tracksTopic, event.aggregateId(), event);
    }

    @Override
    public void sendTrackDeletedEvent(TrackDeletedEvent payload) {
        var event = new CatalogEvent<>(
                UUID.randomUUID(), "TrackDeleted", OffsetDateTime.now(),
                payload.trackId().toString(), "Track", 1, payload);
        log.info("Sending TrackDeletedEvent to Kafka. Event ID: {}", event.eventId());
        kafkaTemplate.send(tracksTopic, event.aggregateId(), event);
    }

    @Override
    public void sendAlbumCreatedEvent(AlbumCreatedEvent payload) {
        var event = new CatalogEvent<>(
                UUID.randomUUID(), "AlbumCreated", OffsetDateTime.now(),
                payload.albumId().toString(), "Album", 1, payload);
        log.info("Sending AlbumCreatedEvent to Kafka. Event ID: {}", event.eventId());
        kafkaTemplate.send(albumsTopic, event.aggregateId(), event);
    }

    @Override
    public void sendAlbumUpdatedEvent(AlbumUpdatedEvent payload) {
        var event = new CatalogEvent<>(
                UUID.randomUUID(), "AlbumUpdated", OffsetDateTime.now(),
                payload.albumId().toString(), "Album", 1, payload);
        log.info("Sending AlbumUpdatedEvent to Kafka. Event ID: {}", event.eventId());
        kafkaTemplate.send(albumsTopic, event.aggregateId(), event);
    }

    @Override
    public void sendAlbumDeletedEvent(AlbumDeletedEvent payload) {
        var event = new CatalogEvent<>(
                UUID.randomUUID(), "AlbumDeleted", OffsetDateTime.now(),
                payload.albumId().toString(), "Album", 1, payload);
        log.info("Sending AlbumDeletedEvent to Kafka. Event ID: {}", event.eventId());
        kafkaTemplate.send(albumsTopic, event.aggregateId(), event);
    }

    @Override
    public void sendArtistCreatedEvent(ArtistCreatedEvent payload) {
        var event = new CatalogEvent<>(
                UUID.randomUUID(), "ArtistCreated", OffsetDateTime.now(),
                payload.artistId().toString(), "Artist", 1, payload);
        log.info("Sending ArtistCreatedEvent to Kafka. Event ID: {}", event.eventId());
        kafkaTemplate.send(artistsTopic, event.aggregateId(), event);
    }

    @Override
    public void sendArtistUpdatedEvent(ArtistUpdatedEvent payload) {
        var event = new CatalogEvent<>(
                UUID.randomUUID(), "ArtistUpdated", OffsetDateTime.now(),
                payload.artistId().toString(), "Artist", 1, payload);
        log.info("Sending ArtistUpdatedEvent to Kafka. Event ID: {}", event.eventId());
        kafkaTemplate.send(artistsTopic, event.aggregateId(), event);
    }

    @Override
    public void sendArtistDeletedEvent(ArtistDeletedEvent payload) {
        var event = new CatalogEvent<>(
                UUID.randomUUID(), "ArtistDeleted", OffsetDateTime.now(),
                payload.artistId().toString(), "Artist", 1, payload);
        log.info("Sending ArtistDeletedEvent to Kafka. Event ID: {}", event.eventId());
        kafkaTemplate.send(artistsTopic, event.aggregateId(), event);
    }

    @Override
    public void sendGenreCreatedEvent(GenreCreatedEvent payload) {
        var event = new CatalogEvent<>(
                UUID.randomUUID(), "GenreCreated", OffsetDateTime.now(),
                payload.genreId().toString(), "Genre", 1, payload);
        log.info("Sending GenreCreatedEvent to Kafka. Event ID: {}", event.eventId());
        kafkaTemplate.send(genresTopic, event.aggregateId(), event);
    }

    @Override
    public void sendGenreUpdatedEvent(GenreUpdatedEvent payload) {
        var event = new CatalogEvent<>(
                UUID.randomUUID(), "GenreUpdated", OffsetDateTime.now(),
                payload.genreId().toString(), "Genre", 1, payload);
        log.info("Sending GenreUpdatedEvent to Kafka. Event ID: {}", event.eventId());
        kafkaTemplate.send(genresTopic, event.aggregateId(), event);
    }

    @Override
    public void sendGenreDeletedEvent(GenreDeletedEvent payload) {
        var event = new CatalogEvent<>(
                UUID.randomUUID(), "GenreDeleted", OffsetDateTime.now(),
                payload.genreId().toString(), "Genre", 1, payload);
        log.info("Sending GenreDeletedEvent to Kafka. Event ID: {}", event.eventId());
        kafkaTemplate.send(genresTopic, event.aggregateId(), event);
    }
}