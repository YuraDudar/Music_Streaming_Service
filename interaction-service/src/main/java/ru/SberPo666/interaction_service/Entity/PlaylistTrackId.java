package ru.SberPo666.interaction_service.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistTrackId implements Serializable {
    private UUID playlistId;
    private UUID trackId;
}
