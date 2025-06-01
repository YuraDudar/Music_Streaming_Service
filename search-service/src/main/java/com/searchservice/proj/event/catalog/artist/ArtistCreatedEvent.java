package com.searchservice.proj.event.catalog.artist;

public record ArtistCreatedEvent(
        Long artistId,
        String name,
        String bio,
        String s3ArtistPhotoKey
) {}