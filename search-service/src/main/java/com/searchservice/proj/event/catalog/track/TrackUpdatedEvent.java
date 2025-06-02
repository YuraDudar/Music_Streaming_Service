package com.searchservice.proj.event.catalog.track;

import java.time.LocalDate;
import java.util.Set;

public record TrackUpdatedEvent(
        Long trackId,
        String title,
        Integer durationMs,
        String audioFileS3Key,
        LocalDate releaseDate,
        Boolean isExplicit,
        Long albumId,
        String albumTitle,
        Set<TrackCreatedEvent.ArtistInfo> artists,
        Set<TrackCreatedEvent.GenreInfo> genres
) {}