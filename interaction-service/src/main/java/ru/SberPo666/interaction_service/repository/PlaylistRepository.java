package ru.SberPo666.interaction_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.SberPo666.interaction_service.Entity.PlaylistEntity;

import java.util.List;
import java.util.UUID;

public interface PlaylistRepository extends JpaRepository<PlaylistEntity, UUID> {

    @Query(value =
            """
            Select * from playlists where user_id = :userId
            """,
    nativeQuery = true)
    List<PlaylistEntity> getPlaylistByUserId(UUID userId);

    @Query(value =
            """
            Select * from playlists where playlist_id = :playlistId
            """,
            nativeQuery = true)
    PlaylistEntity getPlaylistById(UUID playlistId);

    @Query(value =
            """
            Select position from playlist_tracks where playlist_id = :playlistId order by position desc
            """,
            nativeQuery = true)
    Integer getLastPositionInPlaylistById(UUID playlistId);

}
