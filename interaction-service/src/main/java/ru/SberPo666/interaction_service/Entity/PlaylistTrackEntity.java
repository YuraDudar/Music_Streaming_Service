package ru.SberPo666.interaction_service.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "playlist_tracks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(PlaylistTrackId.class)
public class PlaylistTrackEntity {

    @Id
    private UUID playlistId;

    @Id
    private UUID trackId;

    private Integer position;
    private LocalDateTime addedAt;

    @ManyToOne
    @JoinColumn(name = "playlistId", insertable = false, updatable = false)
    private PlaylistEntity playlist;
}
