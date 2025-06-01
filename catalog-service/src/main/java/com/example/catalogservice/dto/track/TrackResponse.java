package com.example.catalogservice.dto.track;

import com.example.catalogservice.dto.album.AlbumSummaryDto;
import com.example.catalogservice.dto.artist.ArtistSummaryDto;
import com.example.catalogservice.dto.genre.GenreSummaryDto;
import lombok.Builder;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;

@Builder
public record TrackResponse(
        Long id,
        String title,
        Integer durationMs,
        String audioFileS3Url,
        LocalDate releaseDate,
        Boolean isExplicit,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,

        AlbumSummaryDto album,
        Set<ArtistSummaryDto> artists,
        Set<GenreSummaryDto> genres
) {}