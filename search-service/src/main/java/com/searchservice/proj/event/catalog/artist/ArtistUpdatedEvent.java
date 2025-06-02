package com.searchservice.proj.event.catalog.artist;

public record ArtistUpdatedEvent(
        Long artistId,
        String name,
        String bio,
        String s3ArtistPhotoKey
) {}
