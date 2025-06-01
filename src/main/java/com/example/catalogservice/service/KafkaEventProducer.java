package com.example.catalogservice.service;

import com.example.catalogservice.event.album.AlbumCreatedEvent;
import com.example.catalogservice.event.album.AlbumDeletedEvent;
import com.example.catalogservice.event.album.AlbumUpdatedEvent;
import com.example.catalogservice.event.artist.ArtistCreatedEvent;
import com.example.catalogservice.event.artist.ArtistUpdatedEvent;
import com.example.catalogservice.event.artist.ArtistDeletedEvent;
import com.example.catalogservice.event.genre.GenreCreatedEvent;
import com.example.catalogservice.event.genre.GenreDeletedEvent;
import com.example.catalogservice.event.genre.GenreUpdatedEvent;
import com.example.catalogservice.event.track.TrackCreatedEvent;
import com.example.catalogservice.event.track.TrackDeletedEvent;
import com.example.catalogservice.event.track.TrackUpdatedEvent;


public interface KafkaEventProducer {

    void sendTrackCreatedEvent(TrackCreatedEvent payload);
    void sendTrackUpdatedEvent(TrackUpdatedEvent payload);
    void sendTrackDeletedEvent(TrackDeletedEvent payload);

    void sendAlbumCreatedEvent(AlbumCreatedEvent payload);
    void sendAlbumUpdatedEvent(AlbumUpdatedEvent payload);
    void sendAlbumDeletedEvent(AlbumDeletedEvent payload);

    void sendArtistCreatedEvent(ArtistCreatedEvent payload);
    void sendArtistUpdatedEvent(ArtistUpdatedEvent payload);
    void sendArtistDeletedEvent(ArtistDeletedEvent payload);

    void sendGenreCreatedEvent(GenreCreatedEvent payload);
    void sendGenreUpdatedEvent(GenreUpdatedEvent payload);
    void sendGenreDeletedEvent(GenreDeletedEvent payload);
}