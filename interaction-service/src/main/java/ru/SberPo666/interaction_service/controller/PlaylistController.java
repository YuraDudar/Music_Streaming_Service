package ru.SberPo666.interaction_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.SberPo666.interaction_service.controller.request.ChangePositionInPlaylistRequest;
import ru.SberPo666.interaction_service.controller.request.CreatePlaylistRequest;
import ru.SberPo666.interaction_service.controller.response.GetPlaylistTrackResponse;
import ru.SberPo666.interaction_service.controller.response.GetPlaylistsResponse;
import ru.SberPo666.interaction_service.service.PlaylistService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @GetMapping("/me/playlists")
    public List<GetPlaylistsResponse> getPlaylistsResponse(@AuthenticationPrincipal Jwt jwt){
        return playlistService.getPlaylistsByUserId(UUID.fromString(jwt.getClaim("user_id")));
    }

    @GetMapping("/me/playlists/{playlistId}")
    public List<GetPlaylistTrackResponse> getTrackByPlaylistId(@AuthenticationPrincipal Jwt jwt, @PathVariable(name = "playlistId") String playlistId){
        return playlistService.getTrackByPlaylistId(UUID.fromString(playlistId), UUID.fromString(jwt.getClaim("user_id")));
    }

    @PostMapping("/me/playlists")
    public void createPlaylist(@AuthenticationPrincipal Jwt jwt, @RequestBody CreatePlaylistRequest request){
        playlistService.createPlaylist(request, UUID.fromString(jwt.getClaim("user_id")));
    }

    @PutMapping("/me/playlists/{playlistId}")
    public void updatePlaylist(@AuthenticationPrincipal Jwt jwt, @RequestBody CreatePlaylistRequest request, @PathVariable(name = "playlistId") String playlistId){
        playlistService.updatePlayList(request, UUID.fromString(jwt.getClaim("user_id")), UUID.fromString(playlistId));
    }

    @DeleteMapping("/me/playlists/{playlistId}")
    public void deletePlaylist(@AuthenticationPrincipal Jwt jwt, @PathVariable(name = "playlistId") String playlistId){
        playlistService.deletePlaylistById(UUID.fromString(jwt.getClaim("user_id")), UUID.fromString(playlistId));
    }

    @PostMapping("/me/playlists/{playlistId}/tracks/{trackId}")
    public void addTrackIntoPlaylist(@AuthenticationPrincipal Jwt jwt, @PathVariable(name = "playlistId") String playlistId, @PathVariable(name = "trackId") String trackId){
        playlistService.addTrackIntoPlaylist(UUID.fromString(playlistId), UUID.fromString(jwt.getClaim("user_id")), UUID.fromString(trackId));
    }

    @DeleteMapping("/me/playlists/{playlistId}/tracks/{trackId}")
    public void deleteTrackFromPlaylist(@AuthenticationPrincipal Jwt jwt, @PathVariable(name = "playlistId") String playlistId, @PathVariable(name = "trackId") String trackId){
        playlistService.deleteTrackInPlaylist(UUID.fromString(playlistId), UUID.fromString(jwt.getClaim("user_id")), UUID.fromString(trackId));
    }

    @PutMapping("/me/playlists/{playlistId}/tracks")
    public void changePositionInPlaylist(@AuthenticationPrincipal Jwt jwt, @PathVariable(name = "playlistId") String playlistId, @RequestBody List<ChangePositionInPlaylistRequest> request){
        playlistService.changePositionsInPlaylist(request, UUID.fromString(jwt.getClaim("user_id")), UUID.fromString(playlistId));
    }

    @GetMapping("/me/histoyr")
    public List<UUID> getHistory(@AuthenticationPrincipal Jwt jwt){
        return playlistService.getHistory(jwt.getClaim("user_id"));
    }

}
