package com.example.catalogservice.event.genre;

public record GenreUpdatedEvent(
        Integer genreId,
        String name,
        String description
) {}