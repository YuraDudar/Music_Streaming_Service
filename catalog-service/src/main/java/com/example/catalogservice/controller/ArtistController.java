package com.example.catalogservice.controller;

import com.example.catalogservice.dto.album.AlbumSummaryDto;
import com.example.catalogservice.dto.artist.ArtistCreateRequest;
import com.example.catalogservice.dto.artist.ArtistResponse;
import com.example.catalogservice.dto.artist.ArtistSummaryDto;
import com.example.catalogservice.dto.artist.ArtistUpdateRequest;
import com.example.catalogservice.dto.track.TrackSummaryDto;
import com.example.catalogservice.service.ArtistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    @PostMapping
    public ResponseEntity<ArtistResponse> createArtist(@Valid @RequestBody ArtistCreateRequest createRequest) {
        ArtistResponse createdArtist = artistService.createArtist(createRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdArtist.id())
                .toUri();
        return ResponseEntity.created(location).body(createdArtist);
    }

    @GetMapping("/{artistId}")
    public ResponseEntity<ArtistResponse> getArtistById(@PathVariable Long artistId) {
        return ResponseEntity.ok(artistService.getArtistById(artistId));
    }

    @GetMapping
    public ResponseEntity<Page<ArtistSummaryDto>> getAllArtists(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(artistService.getAllArtists(pageable));
    }

    @GetMapping("/{artistId}/albums")
    public ResponseEntity<Page<AlbumSummaryDto>> getAlbumsByArtistId(
            @PathVariable Long artistId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(artistService.getAlbumsByArtistId(artistId, pageable));
    }

    @GetMapping("/{artistId}/tracks")
    public ResponseEntity<List<TrackSummaryDto>> getTracksByArtistId(
            @PathVariable Long artistId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(artistService.getTracksByArtistId(artistId, pageable));
    }

    @PutMapping("/{artistId}")
    public ResponseEntity<ArtistResponse> updateArtist(@PathVariable Long artistId,
                                                       @Valid @RequestBody ArtistUpdateRequest updateRequest) {
        return ResponseEntity.ok(artistService.updateArtist(artistId, updateRequest));
    }

    @PatchMapping("/{artistId}")
    public ResponseEntity<ArtistResponse> partialUpdateArtist(@PathVariable Long artistId,
                                                              @RequestBody ArtistUpdateRequest updateRequest) {
        return ResponseEntity.ok(artistService.updateArtist(artistId, updateRequest));
    }

    @PostMapping(value = "/{artistId}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArtistResponse> uploadArtistPhoto(@PathVariable Long artistId,
                                                            @RequestPart("photoFile") MultipartFile photoFile) {
        return ResponseEntity.ok(artistService.uploadArtistPhoto(artistId, photoFile));
    }

    @DeleteMapping("/{artistId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteArtist(@PathVariable Long artistId) {
        artistService.deleteArtist(artistId);
    }
}