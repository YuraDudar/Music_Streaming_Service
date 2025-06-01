package com.example.catalogservice.dto.artist;

import lombok.Builder;

@Builder
public record ArtistUpdateRequest(
        String name,
        String bio
) {}