package ru.SberPo666.interaction_service.controller.request;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class CreatePlaylistRequest {
    private String name;
    private String description;
    private Boolean isPublic;
}
