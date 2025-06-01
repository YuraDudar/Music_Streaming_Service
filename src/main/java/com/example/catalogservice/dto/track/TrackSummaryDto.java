package com.example.catalogservice.dto.track;

import com.example.catalogservice.dto.artist.ArtistMicroDto; // Легковесный DTO для артиста
import lombok.Builder;

import java.util.Set;

@Builder
public record TrackSummaryDto(
        Long id,
        String title,
        Integer durationMs,
        String audioFileS3Url, // Полный URL
        Boolean isExplicit,
        Set<ArtistMicroDto> artists // Или один ArtistMicroDto mainArtist, если логика позволяет
) {}