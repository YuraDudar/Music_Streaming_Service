package ru.SberPo666.interaction_service.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "playlists")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistEntity {

    @Id
    private UUID playlistId;

    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    private Boolean isPublic;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaylistTrackEntity> tracks;
}
