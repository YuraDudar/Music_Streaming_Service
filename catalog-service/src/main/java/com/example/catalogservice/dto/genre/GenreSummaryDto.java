package com.example.catalogservice.dto.genre;

import lombok.Builder;

@Builder
public record GenreSummaryDto(
        Integer id,
        String name
) {}