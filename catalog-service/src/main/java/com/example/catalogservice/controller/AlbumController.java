package com.example.catalogservice.controller;

import com.example.catalogservice.dto.album.AlbumCreateRequest;
import com.example.catalogservice.dto.album.AlbumResponse;
import com.example.catalogservice.dto.album.AlbumSummaryDto;
import com.example.catalogservice.dto.album.AlbumUpdateRequest;
import com.example.catalogservice.dto.track.TrackSummaryDto;
import com.example.catalogservice.service.AlbumService;
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
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping
    public ResponseEntity<AlbumResponse> createAlbum(@Valid @RequestBody AlbumCreateRequest createRequest) {
        AlbumResponse createdAlbum = albumService.createAlbum(createRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdAlbum.id())
                .toUri();
        return ResponseEntity.created(location).body(createdAlbum);
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumResponse> getAlbumById(@PathVariable Long albumId) {
        return ResponseEntity.ok(albumService.getAlbumById(albumId));
    }

    @GetMapping
    public ResponseEntity<Page<AlbumSummaryDto>> getAllAlbums(
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        return ResponseEntity.ok(albumService.getAllAlbums(pageable));
    }

    @GetMapping("/{albumId}/tracks")
    public ResponseEntity<List<TrackSummaryDto>> getTracksByAlbumId(
            @PathVariable Long albumId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(albumService.getTracksByAlbumId(albumId, pageable));
    }

    // Эндпоинт для получения альбомов исполнителя будет в ArtistController

    @PutMapping("/{albumId}")
    public ResponseEntity<AlbumResponse> updateAlbum(@PathVariable Long albumId,
                                                     @Valid @RequestBody AlbumUpdateRequest updateRequest) {
        return ResponseEntity.ok(albumService.updateAlbum(albumId, updateRequest));
    }

    @PatchMapping("/{albumId}")
    public ResponseEntity<AlbumResponse> partialUpdateAlbum(@PathVariable Long albumId,
                                                            @RequestBody AlbumUpdateRequest updateRequest) {
        return ResponseEntity.ok(albumService.updateAlbum(albumId, updateRequest));
    }

    @PostMapping(value = "/{albumId}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AlbumResponse> uploadAlbumCover(@PathVariable Long albumId,
                                                          @RequestPart("coverFile") MultipartFile coverFile) {
        return ResponseEntity.ok(albumService.uploadAlbumCover(albumId, coverFile));
    }

    @DeleteMapping("/{albumId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlbum(@PathVariable Long albumId) {
        albumService.deleteAlbum(albumId);
    }
}