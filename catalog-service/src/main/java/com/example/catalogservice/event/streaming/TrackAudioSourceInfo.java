package com.example.catalogservice.event.streaming;

public record TrackAudioSourceInfo(
        String trackId,
        String s3key,
        String eventType //CREATED, UPDATED, DELETED
) {}
