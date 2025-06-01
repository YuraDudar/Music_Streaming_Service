package ru.SberPo666.interaction_service.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_liked_albums")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(UserLikedAlbumId.class)
public class UserLikedAlbumEntity {

    @Id
    private UUID userId;

    @Id
    private UUID albumId;

    private LocalDateTime likedAt;
}
