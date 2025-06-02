package com.example.catalogservice.event.track;

public record TrackAudioSourceChangedEvent(
        Long trackId,
        String newS3AudioKey,
        String previousS3AudioKey,
        ChangeType changeType
) {
    public enum ChangeType { CREATED, UPDATED, DELETED }
}