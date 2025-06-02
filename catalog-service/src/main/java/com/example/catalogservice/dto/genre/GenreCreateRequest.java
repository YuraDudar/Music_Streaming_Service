package com.example.catalogservice.dto.genre;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record GenreCreateRequest(
        @NotBlank String name,
        String description
) {}