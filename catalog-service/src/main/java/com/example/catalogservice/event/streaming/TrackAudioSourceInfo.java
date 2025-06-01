package com.example.catalogservice.event.streaming; // Замените на ваш пакет

public record TrackAudioSourceInfo(
        String trackId,
        String s3key,
        String eventType //CREATED, UPDATED, DELETED
) {}