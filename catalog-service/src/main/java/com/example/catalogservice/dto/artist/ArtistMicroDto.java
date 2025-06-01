package com.example.catalogservice.dto.artist;

import lombok.Builder;


@Builder
public record ArtistMicroDto(
        Long id,
        String name
) {}