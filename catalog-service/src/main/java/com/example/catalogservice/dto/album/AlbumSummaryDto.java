package com.example.catalogservice.dto.album;

import com.example.catalogservice.dto.artist.ArtistMicroDto;
import lombok.Builder;
import java.time.LocalDate;
import java.util.Set;

@Builder
public record AlbumSummaryDto(
        Long id,
        String title,
        String s3AlbumCoverUrl,
        LocalDate releaseDate,
        String albumType,
        Set<ArtistMicroDto> artists // Основные исполнители для отображения
) {}