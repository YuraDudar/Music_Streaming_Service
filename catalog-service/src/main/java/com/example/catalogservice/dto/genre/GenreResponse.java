package com.example.catalogservice.dto.genre;

import lombok.Builder;
import java.time.OffsetDateTime;

@Builder
public record GenreResponse(
        Integer id,
        String name,
        String description,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}