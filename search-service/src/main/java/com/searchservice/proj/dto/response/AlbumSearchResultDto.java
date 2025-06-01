package com.searchservice.proj.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class AlbumSearchResultDto {
    private String id;
    private String title;
    private String albumType;
    private LocalDate releaseDate;
    private String s3CoverUrl;

    private List<TrackSearchResultDto.ArtistInfoForSearch> artists; // Переиспользуем

    private Double score;
}