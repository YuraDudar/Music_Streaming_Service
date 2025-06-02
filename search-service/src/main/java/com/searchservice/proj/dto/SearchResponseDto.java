package com.searchservice.proj.dto;

import com.searchservice.proj.dto.response.AlbumSearchResultDto;
import com.searchservice.proj.dto.response.ArtistSearchResultDto;
import com.searchservice.proj.dto.response.TrackSearchResultDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchResponseDto {
    private List<TrackSearchResultDto> tracks;
    private List<AlbumSearchResultDto> albums;
    private List<ArtistSearchResultDto> artists;

}