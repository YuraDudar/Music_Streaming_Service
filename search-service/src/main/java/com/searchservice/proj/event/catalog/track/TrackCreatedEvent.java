package com.searchservice.proj.event.catalog.track;

import java.time.LocalDate;
import java.util.Set;

public record TrackCreatedEvent(
        Long trackId,
        String title,
        Integer durationMs,
        String audioFileS3Key,
        LocalDate releaseDate,
        Boolean isExplicit,
        Long albumId,
        String albumTitle,
        Set<ArtistInfo> artists,
        Set<GenreInfo> genres
) {
    public record ArtistInfo(Long artistId, String name) {}
    public record GenreInfo(Integer genreId, String name) {}
}