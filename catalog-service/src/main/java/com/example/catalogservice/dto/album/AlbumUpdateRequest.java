package com.example.catalogservice.dto.album;

import lombok.Builder;
import java.time.LocalDate;
import java.util.Set;

@Builder
public record AlbumUpdateRequest(
        String title,
        LocalDate releaseDate,
        String albumType,
        Set<Long> artistIds,
        Set<Integer> genreIds
) {}