package com.example.catalogservice.dto.artist;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ArtistCreateRequest(
        @NotBlank String name,
        String bio
) {}