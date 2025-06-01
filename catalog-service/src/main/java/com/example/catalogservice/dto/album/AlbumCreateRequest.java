package com.example.catalogservice.dto.album;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import java.time.LocalDate;
import java.util.Set;

@Builder
public record AlbumCreateRequest(
        @NotBlank String title,
        LocalDate releaseDate,
        @NotNull String albumType,
        @NotEmpty Set<Long> artistIds,
        Set<Integer> genreIds
) {}