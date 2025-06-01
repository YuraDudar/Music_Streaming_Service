package com.example.catalogservice.controller;

import com.example.catalogservice.dto.genre.GenreCreateRequest;
import com.example.catalogservice.dto.genre.GenreResponse;
import com.example.catalogservice.dto.genre.GenreUpdateRequest;
import com.example.catalogservice.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @PostMapping
    public ResponseEntity<GenreResponse> createGenre(@Valid @RequestBody GenreCreateRequest createRequest) {
        GenreResponse createdGenre = genreService.createGenre(createRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdGenre.id())
                .toUri();
        return ResponseEntity.created(location).body(createdGenre);
    }

    @GetMapping("/{genreId}")
    public ResponseEntity<GenreResponse> getGenreById(@PathVariable Integer genreId) {
        return ResponseEntity.ok(genreService.getGenreById(genreId));
    }

    @GetMapping
    public ResponseEntity<List<GenreResponse>> getAllGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    @PutMapping("/{genreId}")
    public ResponseEntity<GenreResponse> updateGenre(@PathVariable Integer genreId,
                                                     @Valid @RequestBody GenreUpdateRequest updateRequest) {
        return ResponseEntity.ok(genreService.updateGenre(genreId, updateRequest));
    }


    @DeleteMapping("/{genreId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGenre(@PathVariable Integer genreId) {
        genreService.deleteGenre(genreId);
    }
}