package com.searchservice.proj.service;

import com.searchservice.proj.event.catalog.album.AlbumCreatedEvent;
import com.searchservice.proj.event.catalog.album.AlbumUpdatedEvent;
import com.searchservice.proj.event.catalog.artist.ArtistCreatedEvent;
import com.searchservice.proj.event.catalog.artist.ArtistUpdatedEvent;
import com.searchservice.proj.event.catalog.track.TrackCreatedEvent;
import com.searchservice.proj.event.catalog.track.TrackUpdatedEvent;


public interface IndexingService {

    void indexTrack(TrackCreatedEvent eventPayload);
    void updateTrack(TrackUpdatedEvent eventPayload);
    void deleteTrack(Long trackId);

    void indexAlbum(AlbumCreatedEvent eventPayload);
    void updateAlbum(AlbumUpdatedEvent eventPayload);
    void deleteAlbum(Long albumId);

    void indexArtist(ArtistCreatedEvent eventPayload);
    void updateArtist(ArtistUpdatedEvent eventPayload);
    void deleteArtist(Long artistId);

    void handleArtistUpdateInRelatedDocuments(ArtistUpdatedEvent artistPayload);

}