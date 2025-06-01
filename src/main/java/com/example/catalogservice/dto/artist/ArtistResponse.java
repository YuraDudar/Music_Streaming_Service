package com.example.catalogservice.dto.artist;

import lombok.Builder;
import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record ArtistResponse(
        Long id,
        String name,
        String bio,
        String s3ArtistPhotoUrl,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt

) {}