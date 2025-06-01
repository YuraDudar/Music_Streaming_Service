package com.example.catalogservice.dto.track;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import java.time.LocalDate;
import java.util.Set;

@Builder
public record TrackCreateRequest(
        @NotBlank String title,
        @NotNull @Positive Integer durationMs,
        @NotBlank String audioFileS3Key, // Ключ S3, предполагаем, что файл уже загружен или его ключ известен
        LocalDate releaseDate,
        Boolean isExplicit,

        Long albumId, // Опционально. Если null - создается новый альбом типа "SINGLE"

        @NotEmpty Set<Long> artistIds, // ID существующих исполнителей
        Set<Integer> genreIds      // ID существующих жанров (могут быть опциональны при создании)
) {}