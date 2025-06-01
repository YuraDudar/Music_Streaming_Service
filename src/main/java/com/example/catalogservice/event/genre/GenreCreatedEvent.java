package com.example.catalogservice.event.genre;


public record GenreCreatedEvent(
        Integer genreId,
        String name,
        String description
) {}