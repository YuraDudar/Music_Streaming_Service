package ru.SberPo666.interaction_service.controller.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class GetPlaylistsResponse {
    private UUID playlistId;
    private UUID userId;
    private String name;
    private String description;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
