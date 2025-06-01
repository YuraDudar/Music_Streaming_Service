package ru.SberPo666.interaction_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.SberPo666.interaction_service.Entity.PlaylistEntity;
import ru.SberPo666.interaction_service.Entity.PlaylistTrackEntity;
import ru.SberPo666.interaction_service.controller.request.ChangePositionInPlaylistRequest;
import ru.SberPo666.interaction_service.controller.request.CreatePlaylistRequest;
import ru.SberPo666.interaction_service.controller.response.GetPlaylistTrackResponse;
import ru.SberPo666.interaction_service.controller.response.GetPlaylistsResponse;
import ru.SberPo666.interaction_service.repository.HistoryRepository;
import ru.SberPo666.interaction_service.repository.PlaylistRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final HistoryRepository historyRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Cacheable(value = "userPlaylists", key = "#userId")
    public List<GetPlaylistsResponse> getPlaylistsByUserId(UUID userId){
        return playlistRepository.getPlaylistByUserId(userId).stream()
                .map((entity -> GetPlaylistsResponse.builder()
                        .userId(entity.getUserId())
                        .playlistId(entity.getPlaylistId())
                        .name(entity.getName())
                        .description(entity.getDescription())
                        .isPublic(entity.getIsPublic())
                        .updatedAt(entity.getUpdatedAt())
                        .createdAt(entity.getCreatedAt())
                        .build()))
                .toList();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "playlistById", key = "#playlistId"),
            @CacheEvict(value = "userPlaylists", key = "#userId"),
            @CacheEvict(value = "playlistTracksById", key = "#playlistId")
    })
    public void deletePlaylistById(UUID userId, UUID playlistId){
        if(!playlistRepository.existsById(playlistId))
            throw new RuntimeException(String.format("Playlist with id %s does not exists", playlistId.toString()));
        PlaylistEntity entity = playlistRepository.getPlaylistById(playlistId);
        if(!entity.getUserId().equals(userId))
            throw new RuntimeException("User does not have permission to view this playlist");
        playlistRepository.deleteById(playlistId);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "userPlaylists", key = "#userID")
            }
    )
    public void createPlaylist(CreatePlaylistRequest request, UUID userID, UUID newUUID){

        PlaylistEntity entity = PlaylistEntity.builder()
                .isPublic(request.getIsPublic())
                .name(request.getName())
                .description(request.getDescription())
                .userId(userID)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .playlistId(newUUID)
                .build();

        playlistRepository.save(entity);

    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "playlistById", key = "#playlistId"),
                    @CacheEvict(value = "userPlaylists", key = "#userId")
            }
    )
    public void updatePlayList(CreatePlaylistRequest request, UUID userId, UUID playlistId){

        if(!playlistRepository.existsById(playlistId))
            throw new RuntimeException(String.format("Playlist with id %s does not exists", playlistId.toString()));
        PlaylistEntity entityToUpdate = playlistRepository.getPlaylistById(playlistId);
        if(!entityToUpdate.getUserId().equals(userId))
            throw new RuntimeException("User does not have permission to view this playlist");


        entityToUpdate.setName(request.getName());
        entityToUpdate.setDescription(request.getDescription());
        entityToUpdate.setIsPublic(request.getIsPublic());
        entityToUpdate.setUpdatedAt(LocalDateTime.now());

        playlistRepository.save(entityToUpdate);
    }

    @Cacheable(value = "playlistTracksById", key = "#playlistId")
    public List<GetPlaylistTrackResponse> getTrackByPlaylistId(UUID playlistId, UUID userId){
        if(!playlistRepository.existsById(playlistId))
            throw new RuntimeException(String.format("Playlist with id %s does not exists", playlistId.toString()));
        PlaylistEntity entity = playlistRepository.getPlaylistById(playlistId);
        if(!entity.getIsPublic() && !entity.getUserId().equals(userId))
            throw new RuntimeException("User does not have permission to view this playlist");
        return entity.getTracks().stream()
                .map((track) -> GetPlaylistTrackResponse.builder()
                        .trackId(track.getTrackId())
                        .addedAt(track.getAddedAt())
                        .position(track.getPosition())
                        .build())
                .toList();
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "playlistTracksById", key = "#playlistId")
            }
    )
    public void addTrackIntoPlaylist(UUID playlistId, UUID userId, UUID trackId){
        if(!playlistRepository.existsById(playlistId))
            throw new RuntimeException(String.format("Playlist with id %s does not exists", playlistId.toString()));
        PlaylistEntity entity = playlistRepository.getPlaylistById(playlistId);
        if(!entity.getUserId().equals(userId))
            throw new RuntimeException("User does not have permission to view this playlist");
        PlaylistTrackEntity newEntity = PlaylistTrackEntity.builder()
                .trackId(trackId)
                .addedAt(LocalDateTime.now())
                .playlistId(playlistId)
                .position(playlistRepository.getLastPositionInPlaylistById(playlistId) == null ? 0 : playlistRepository.getLastPositionInPlaylistById(playlistId) + 1)
                .build();
        entity.getTracks().add(newEntity);
        playlistRepository.save(entity);

        if(entity.getName().equals("likes")){
            kafkaTemplate.send("likes-topic", trackId.toString());
        }

    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "playlistTracksById", key = "#playlistId")
            }
    )
    public void deleteTrackInPlaylist(UUID playlistId, UUID userId, UUID trackId){
        if(!playlistRepository.existsById(playlistId))
            throw new RuntimeException(String.format("Playlist with id %s does not exists", playlistId.toString()));
        PlaylistEntity entity = playlistRepository.getPlaylistById(playlistId);
        if(!entity.getUserId().equals(userId))
            throw new RuntimeException("User does not have permission to view this playlist");
        entity.getTracks().removeIf(it -> it.getTrackId().equals(trackId));
        playlistRepository.save(entity);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "playlistTracksById", key = "#playlistId")
            }
    )
    public void changePositionsInPlaylist(List<ChangePositionInPlaylistRequest> request, UUID userId, UUID playlistId){
        if(!playlistRepository.existsById(playlistId))
            throw new RuntimeException(String.format("Playlist with id %s does not exists", playlistId.toString()));
        PlaylistEntity ent = playlistRepository.getPlaylistById(playlistId);
        if(!ent.getUserId().equals(userId))
            throw new RuntimeException("User does not have permission to view this playlist");
        Map<String, Integer> map = request.stream().collect(Collectors.toMap(pos -> pos.getTrackId().toString(), ChangePositionInPlaylistRequest::getPosition));
        PlaylistEntity entity = playlistRepository.getPlaylistById(playlistId);
        entity.getTracks().forEach(track -> track.setPosition(map.get(track.getTrackId().toString())));
        playlistRepository.save(entity);
    }


    public List<UUID> getHistory(UUID userId){
        return historyRepository.getHistoryByUserId(userId);
    }

}
