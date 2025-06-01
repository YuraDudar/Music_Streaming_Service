package com.example.catalogservice.service;

import com.example.catalogservice.dto.album.AlbumCreateRequest;
import com.example.catalogservice.dto.album.AlbumResponse;
import com.example.catalogservice.dto.album.AlbumSummaryDto;
import com.example.catalogservice.dto.album.AlbumUpdateRequest;
import com.example.catalogservice.dto.track.TrackSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface AlbumService {
    AlbumResponse createAlbum(AlbumCreateRequest createRequest);
    AlbumResponse getAlbumById(Long albumId);
    Page<AlbumSummaryDto> getAllAlbums(Pageable pageable /*, FilterParams */);
    Page<AlbumSummaryDto> getAlbumsByArtistId(Long artistId, Pageable pageable);
    List<TrackSummaryDto> getTracksByAlbumId(Long albumId, Pageable pageable);
    AlbumResponse updateAlbum(Long albumId, AlbumUpdateRequest updateRequest);
    AlbumResponse uploadAlbumCover(Long albumId, MultipartFile coverFile);
    void deleteAlbum(Long albumId);
}