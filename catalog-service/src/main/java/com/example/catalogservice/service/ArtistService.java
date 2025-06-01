package com.example.catalogservice.service;
import com.example.catalogservice.dto.album.AlbumSummaryDto;
import com.example.catalogservice.dto.artist.ArtistCreateRequest;
import com.example.catalogservice.dto.artist.ArtistResponse;
import com.example.catalogservice.dto.artist.ArtistSummaryDto;
import com.example.catalogservice.dto.artist.ArtistUpdateRequest;
import com.example.catalogservice.dto.track.TrackSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArtistService {
    ArtistResponse createArtist(ArtistCreateRequest createRequest);
    ArtistResponse getArtistById(Long artistId);
    Page<ArtistSummaryDto> getAllArtists(Pageable pageable);
    Page<AlbumSummaryDto> getAlbumsByArtistId(Long artistId, Pageable pageable);
    List<TrackSummaryDto> getTracksByArtistId(Long artistId, Pageable pageable);
    ArtistResponse updateArtist(Long artistId, ArtistUpdateRequest updateRequest);
    ArtistResponse uploadArtistPhoto(Long artistId, MultipartFile photoFile);
    void deleteArtist(Long artistId);
}