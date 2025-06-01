package com.example.catalogservice.event.artist;

public record ArtistCreatedEvent(
        Long artistId,
        String name,
        String bio,
        String s3ArtistPhotoKey
) {}