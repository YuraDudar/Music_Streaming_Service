package com.example.catalogservice.dto.album;

import com.example.catalogservice.dto.artist.ArtistSummaryDto;
import com.example.catalogservice.dto.genre.GenreSummaryDto;
import lombok.Builder;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;

@Builder
public record AlbumResponse(
        Long id,
        String title,
        LocalDate releaseDate,
        String albumType,
        String s3AlbumCoverUrl,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        Set<ArtistSummaryDto> artists,
        Set<GenreSummaryDto> genres
) {}