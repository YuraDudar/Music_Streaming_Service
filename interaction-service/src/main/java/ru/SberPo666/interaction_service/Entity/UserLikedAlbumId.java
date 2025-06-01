package ru.SberPo666.interaction_service.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLikedAlbumId implements Serializable {
    private UUID userId;
    private UUID albumId;
}
