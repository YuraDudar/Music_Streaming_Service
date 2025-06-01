package com.searchservice.proj.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArtistSearchResultDto {
    private String id;
    private String name;
    private String s3PhotoUrl;

    private Double score;
}