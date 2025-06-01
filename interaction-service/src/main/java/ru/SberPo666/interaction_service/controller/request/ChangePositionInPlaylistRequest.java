package ru.SberPo666.interaction_service.controller.request;

import lombok.Data;

import java.util.UUID;

@Data
public class ChangePositionInPlaylistRequest {
    private UUID trackId;
    private Integer position;
}
