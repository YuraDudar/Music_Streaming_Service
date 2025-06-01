package com.searchservice.proj.event.catalog.album;


import com.searchservice.proj.event.catalog.track.TrackCreatedEvent;

import java.time.LocalDate;
import java.util.Set;

public record AlbumCreatedEvent(
        Long albumId,
        String title,
        LocalDate releaseDate,
        String albumType,
        String s3AlbumCoverKey, // Ключ обложки
        Set<TrackCreatedEvent.ArtistInfo> artists,
        Set<TrackCreatedEvent.GenreInfo> genres
) {}