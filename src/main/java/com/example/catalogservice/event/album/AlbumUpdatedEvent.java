package com.example.catalogservice.event.album;

import com.example.catalogservice.event.track.TrackCreatedEvent;

import java.time.LocalDate;
import java.util.Set;

public record AlbumUpdatedEvent(
        Long albumId,
        String title,
        LocalDate releaseDate,
        String albumType,
        String s3AlbumCoverKey,
        Set<TrackCreatedEvent.ArtistInfo> artists,
        Set<TrackCreatedEvent.GenreInfo> genres
) {}