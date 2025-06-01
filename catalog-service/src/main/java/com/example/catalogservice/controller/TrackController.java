package com.example.catalogservice.controller;

import com.example.catalogservice.dto.track.TrackCreateRequest;
import com.example.catalogservice.dto.track.TrackResponse;
import com.example.catalogservice.dto.track.TrackSummaryDto;
import com.example.catalogservice.dto.track.TrackUpdateRequest;
import com.example.catalogservice.service.TrackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping("/api/v1/tracks")
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;

    @PostMapping
    public ResponseEntity<TrackResponse> createTrack(@Valid @RequestBody TrackCreateRequest createRequest) {
        TrackResponse createdTrack = trackService.createTrack(createRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTrack.id())
                .toUri();
        return ResponseEntity.created(location).body(createdTrack);
    }

    @GetMapping("/{trackId}")
    public ResponseEntity<TrackResponse> getTrackById(@PathVariable Long trackId) {
        return ResponseEntity.ok(trackService.getTrackById(trackId));
    }

    @GetMapping
    public ResponseEntity<Page<TrackSummaryDto>> getAllTracks(
            @PageableDefault(size = 20, sort = "title") Pageable pageable) {
        return ResponseEntity.ok(trackService.getAllTracks(pageable));
    }
    @PutMapping("/{trackId}")
    public ResponseEntity<TrackResponse> updateTrack(@PathVariable Long trackId,
                                                     @Valid @RequestBody TrackUpdateRequest updateRequest) {
        return ResponseEntity.ok(trackService.updateTrack(trackId, updateRequest));
    }

    @PatchMapping("/{trackId}")
    public ResponseEntity<TrackResponse> partialUpdateTrack(@PathVariable Long trackId,
                                                            @RequestBody TrackUpdateRequest updateRequest) {

        return ResponseEntity.ok(trackService.updateTrack(trackId, updateRequest));
    }

    @DeleteMapping("/{trackId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrack(@PathVariable Long trackId) {
        trackService.deleteTrack(trackId);
    }
}