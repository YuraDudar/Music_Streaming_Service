package com.example.catalogservice.dto.track;

import lombok.Builder;
import java.time.LocalDate;
import java.util.Set;

@Builder
public record TrackUpdateRequest(
        String title,
        Integer durationMs,
        String audioFileS3Key,
        LocalDate releaseDate,
        Boolean isExplicit,
        Long albumId,
        Set<Long> artistIds,
        Set<Integer> genreIds
) {}