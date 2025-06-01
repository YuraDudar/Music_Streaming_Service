package com.example.catalogservice.event.artist;

public record ArtistUpdatedEvent(
        Long artistId,
        String name,
        String bio,
        String s3ArtistPhotoKey
) {}