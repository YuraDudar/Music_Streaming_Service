package ru.SberPo666.interaction_service.repository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    @Cacheable(value = "playlistsByUserId", key = "#userId")
    List<PlaylistEntity> getPlaylistByUserId(UUID userId);

    @Query(value =
            """
            Select * from playlists where playlist_id = :playlistId
            """,
            nativeQuery = true)
    @Cacheable(value = "playlistById", key = "#playlistId")
    PlaylistEntity getPlaylistById(UUID playlistId);

    @Query(value =
            """
            Select position from playlist_tracks where playlist_id = :playlistId order by position desc limit 1
            """,
            nativeQuery = true)
    @Cacheable(value = "playlistLastPosition", key = "#playlistId")
    Integer getLastPositionInPlaylistById(UUID playlistId);

    @Override
    @Caching(
            put = { @CachePut(value = "playlistById", key = "#result.id") },
            evict = {
                    @CacheEvict(value = "playlistsByUserId", key = "#result.userId")
            }
    )
    <S extends PlaylistEntity> S save(S entity);

    @Override
    @Caching(evict = {
            @CacheEvict(value = "playlistById", key = "#id"),
            @CacheEvict(value = "playlistLastPosition", key = "#id")
    })
    void deleteById(UUID id);

}
