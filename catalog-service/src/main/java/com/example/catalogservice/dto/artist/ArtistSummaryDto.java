package com.example.catalogservice.dto.artist;

import lombok.Builder;

@Builder
public record ArtistSummaryDto(
        Long id,
        String name,
        String s3ArtistPhotoUrl
) {}