package com.searchservice.proj.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class TrackSearchResultDto {
    private String id;
    private String title;
    private Integer durationMs;
    private String audioFileS3Url;
    private Boolean isExplicit;
    private LocalDate releaseDate;

    private AlbumInfoForSearch album;
    private List<ArtistInfoForSearch> artists;

    private Double score;

    @Data
    @Builder
    public static class AlbumInfoForSearch {
        private String id;
        private String title;
        private String s3CoverUrl;
    }

    @Data
    @Builder
    public static class ArtistInfoForSearch {
        private String id;
        private String name;
    }
}