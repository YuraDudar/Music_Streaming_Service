package ru.SberPo666.interaction_service.controller.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class GetPlaylistTrackResponse {
    private UUID trackId;
    private Integer position;
    private LocalDateTime addedAt;
}
