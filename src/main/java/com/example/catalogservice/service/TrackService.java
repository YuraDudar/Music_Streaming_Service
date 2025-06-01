package com.example.catalogservice.service; // Замените на ваш пакет

import com.example.catalogservice.dto.track.TrackCreateRequest;
import com.example.catalogservice.dto.track.TrackResponse;
import com.example.catalogservice.dto.track.TrackSummaryDto;
import com.example.catalogservice.dto.track.TrackUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TrackService {
    TrackResponse createTrack(TrackCreateRequest createRequest);
    TrackResponse getTrackById(Long trackId);
    Page<TrackSummaryDto> getAllTracks(Pageable pageable);
    List<TrackSummaryDto> getTracksByAlbumId(Long albumId, Pageable pageable);
    List<TrackSummaryDto> getTracksByArtistId(Long artistId, Pageable pageable);
    TrackResponse updateTrack(Long trackId, TrackUpdateRequest updateRequest);
    void deleteTrack(Long trackId);
}