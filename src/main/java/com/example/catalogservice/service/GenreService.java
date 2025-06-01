package com.example.catalogservice.service;

import com.example.catalogservice.dto.genre.GenreCreateRequest;
import com.example.catalogservice.dto.genre.GenreResponse;
import com.example.catalogservice.dto.genre.GenreUpdateRequest;

import java.util.List;

public interface GenreService {
    GenreResponse createGenre(GenreCreateRequest createRequest);
    GenreResponse getGenreById(Integer genreId);
    List<GenreResponse> getAllGenres();
    GenreResponse updateGenre(Integer genreId, GenreUpdateRequest updateRequest);
    void deleteGenre(Integer genreId);
}