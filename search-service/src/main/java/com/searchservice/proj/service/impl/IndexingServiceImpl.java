package com.searchservice.proj.service.impl;

import com.searchservice.proj.document.AlbumDocument;
import com.searchservice.proj.document.ArtistDocument;
import com.searchservice.proj.document.TrackDocument;
import com.searchservice.proj.event.catalog.album.AlbumCreatedEvent;
import com.searchservice.proj.event.catalog.album.AlbumUpdatedEvent;
import com.searchservice.proj.event.catalog.artist.ArtistCreatedEvent;
import com.searchservice.proj.event.catalog.artist.ArtistUpdatedEvent;
import com.searchservice.proj.event.catalog.track.TrackCreatedEvent;
import com.searchservice.proj.event.catalog.track.TrackUpdatedEvent;
import com.searchservice.proj.mapper.AlbumEventMapper;
import com.searchservice.proj.mapper.ArtistEventMapper;
import com.searchservice.proj.mapper.TrackEventMapper;
import com.searchservice.proj.repository.AlbumEsRepository;
import com.searchservice.proj.repository.ArtistEsRepository;
import com.searchservice.proj.repository.TrackEsRepository;
import com.searchservice.proj.service.IndexingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService {

    private final TrackEsRepository trackEsRepository;
    private final AlbumEsRepository albumEsRepository;
    private final ArtistEsRepository artistEsRepository;

    private final TrackEventMapper trackEventMapper;
    private final AlbumEventMapper albumEventMapper;
    private final ArtistEventMapper artistEventMapper;

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void indexTrack(TrackCreatedEvent eventPayload) {
        log.info("Indexing new track from event. Track ID: {}", eventPayload.trackId());
        try {
            TrackDocument trackDocument = trackEventMapper.toDocument(eventPayload);
            trackDocument.setSearchTextAll(buildSearchTextForTrack(trackDocument));
            trackEsRepository.save(trackDocument);
            log.info("Track with ID: {} successfully indexed.", trackDocument.getId());
        } catch (Exception e) {
            log.error("Error indexing new track with ID {}: {}", eventPayload.trackId(), e.getMessage(), e);
        }
    }

    @Override
    public void updateTrack(TrackUpdatedEvent eventPayload) {
        String trackIdStr = String.valueOf(eventPayload.trackId());
        log.info("Updating track in index from event. Track ID: {}", trackIdStr);
        try {
            Optional<TrackDocument> existingDocOpt = trackEsRepository.findById(trackIdStr);
            if (existingDocOpt.isPresent()) {
                TrackDocument existingDoc = existingDocOpt.get();
                trackEventMapper.updateDocument(eventPayload, existingDoc);
                existingDoc.setSearchTextAll(buildSearchTextForTrack(existingDoc));
                existingDoc.setUpdatedAt(OffsetDateTime.now());
                trackEsRepository.save(existingDoc);
                log.info("Track with ID: {} successfully updated in index.", trackIdStr);
            } else {
                log.warn("Track with ID: {} not found in index for update. Attempting to index as new.", trackIdStr);
                TrackCreatedEvent createEventEquivalent = convertTrackUpdatedToCreated(eventPayload); // Нужен метод конвертации
                indexTrack(createEventEquivalent);
            }
        } catch (Exception e) {
            log.error("Error updating track with ID {}: {}", trackIdStr, e.getMessage(), e);
        }
    }

    @Override
    public void deleteTrack(Long trackId) {
        String trackIdStr = String.valueOf(trackId);
        log.info("Deleting track from index. Track ID: {}", trackIdStr);
        try {
            if (trackEsRepository.existsById(trackIdStr)) {
                trackEsRepository.deleteById(trackIdStr);
                log.info("Track with ID: {} successfully deleted from index.", trackIdStr);
            } else {
                log.warn("Track with ID: {} not found in index for deletion.", trackIdStr);
            }
        } catch (Exception e) {
            log.error("Error deleting track with ID {}: {}", trackIdStr, e.getMessage(), e);
        }
    }

    @Override
    public void indexAlbum(AlbumCreatedEvent eventPayload) {
        log.info("Indexing new album from event. Album ID: {}", eventPayload.albumId());
        try {
            AlbumDocument albumDocument = albumEventMapper.toDocument(eventPayload);
            albumDocument.setSearchTextAll(buildSearchTextForAlbum(albumDocument));
            albumEsRepository.save(albumDocument);
            log.info("Album with ID: {} successfully indexed.", albumDocument.getId());
        } catch (Exception e) {
            log.error("Error indexing new album with ID {}: {}", eventPayload.albumId(), e.getMessage(), e);
        }
    }

    @Override
    public void updateAlbum(AlbumUpdatedEvent eventPayload) {
        String albumIdStr = String.valueOf(eventPayload.albumId());
        log.info("Updating album in index from event. Album ID: {}", albumIdStr);
        try {
            Optional<AlbumDocument> existingDocOpt = albumEsRepository.findById(albumIdStr);
            if (existingDocOpt.isPresent()) {
                AlbumDocument existingDoc = existingDocOpt.get();
                albumEventMapper.updateDocument(eventPayload, existingDoc);
                existingDoc.setSearchTextAll(buildSearchTextForAlbum(existingDoc));
                existingDoc.setUpdatedAt(OffsetDateTime.now());
                albumEsRepository.save(existingDoc);
                log.info("Album with ID: {} successfully updated in index.", albumIdStr);
            } else {
                log.warn("Album with ID: {} not found for update. Attempting to index as new.", albumIdStr);
                AlbumCreatedEvent createEventEquivalent = convertAlbumUpdatedToCreated(eventPayload);
                indexAlbum(createEventEquivalent);
            }
        } catch (Exception e) {
            log.error("Error updating album with ID {}: {}", albumIdStr, e.getMessage(), e);
        }
    }

    @Override
    public void deleteAlbum(Long albumId) {
        String albumIdStr = String.valueOf(albumId);
        log.info("Deleting album from index. Album ID: {}", albumIdStr);
        try {
            if (albumEsRepository.existsById(albumIdStr)) {
                albumEsRepository.deleteById(albumIdStr);
                log.info("Album with ID: {} successfully deleted from index.", albumIdStr);
                clearAlbumInfoFromTracks(albumIdStr);
            } else {
                log.warn("Album with ID: {} not found for deletion.", albumIdStr);
            }
        } catch (Exception e) {
            log.error("Error deleting album with ID {}: {}", albumIdStr, e.getMessage(), e);
        }
    }

    @Override
    public void indexArtist(ArtistCreatedEvent eventPayload) {
        log.info("Indexing new artist from event. Artist ID: {}", eventPayload.artistId());
        try {
            ArtistDocument artistDocument = artistEventMapper.toDocument(eventPayload);
            artistDocument.setSearchTextAll(buildSearchTextForArtist(artistDocument));
            artistEsRepository.save(artistDocument);
            log.info("Artist with ID: {} successfully indexed.", artistDocument.getId());
        } catch (Exception e) {
            log.error("Error indexing new artist with ID {}: {}", eventPayload.artistId(), e.getMessage(), e);
        }
    }

    @Override
    public void updateArtist(ArtistUpdatedEvent eventPayload) {
        String artistIdStr = String.valueOf(eventPayload.artistId());
        log.info("Updating artist in index. Artist ID: {}", artistIdStr);
        try {
            Optional<ArtistDocument> existingDocOpt = artistEsRepository.findById(artistIdStr);
            if (existingDocOpt.isPresent()) {
                ArtistDocument existingDoc = existingDocOpt.get();
                artistEventMapper.updateDocument(eventPayload, existingDoc);
                existingDoc.setSearchTextAll(buildSearchTextForArtist(existingDoc));
                existingDoc.setUpdatedAt(OffsetDateTime.now());
                artistEsRepository.save(existingDoc);
                log.info("Artist with ID: {} successfully updated.", artistIdStr);
                handleArtistUpdateInRelatedDocuments(eventPayload);
            } else {
                log.warn("Artist with ID: {} not found for update. Attempting to index as new.", artistIdStr);
                ArtistCreatedEvent createEventEquivalent = convertArtistUpdatedToCreated(eventPayload);
                indexArtist(createEventEquivalent);
            }
        } catch (Exception e) {
            log.error("Error updating artist with ID {}: {}", artistIdStr, e.getMessage(), e);
        }
    }

    @Override
    public void deleteArtist(Long artistId) {
        String artistIdStr = String.valueOf(artistId);
        log.info("Deleting artist from index. Artist ID: {}", artistIdStr);
        try {
            if (artistEsRepository.existsById(artistIdStr)) {
                artistEsRepository.deleteById(artistIdStr);
                log.info("Artist with ID: {} successfully deleted.", artistIdStr);
                deleteArtistFromTracks(artistIdStr);
                deleteArtistFromAlbums(artistIdStr);
            } else {
                log.warn("Artist with ID: {} not found for deletion.", artistIdStr);
            }
        } catch (Exception e) {
            log.error("Error deleting artist with ID {}: {}", artistIdStr, e.getMessage(), e);
        }
    }

    @Override
    public void handleArtistUpdateInRelatedDocuments(ArtistUpdatedEvent artistPayload) {
        String artistIdStr = String.valueOf(artistPayload.artistId());
        String newArtistName = artistPayload.name();
        log.info("Handling artist update for ID: {} in related documents. New name: {}", artistIdStr, newArtistName);

        List<UpdateQuery> updates = new ArrayList<>();

        Query trackQuery = new CriteriaQuery(new Criteria("artists.id").is(artistIdStr));
        SearchHits<TrackDocument> trackHits = elasticsearchOperations.search(trackQuery, TrackDocument.class);
        if (trackHits.hasSearchHits()) {
            for (SearchHit<TrackDocument> hit : trackHits.getSearchHits()) {
                TrackDocument track = hit.getContent();
                boolean modified = false;
                if (track.getArtists() != null) {
                    for (TrackDocument.ArtistInfoInTrack artistInfo : track.getArtists()) {
                        if (artistIdStr.equals(artistInfo.getId()) && !newArtistName.equals(artistInfo.getName())) {
                            artistInfo.setName(newArtistName);
                            modified = true;
                        }
                    }
                }
                if (modified) {
                    track.setSearchTextAll(buildSearchTextForTrack(track));
                    track.setUpdatedAt(OffsetDateTime.now());

                    trackEsRepository.save(track);
                }
            }
            log.info("Processed artist name update for {} tracks related to artist ID: {}", trackHits.getTotalHits(), artistIdStr);
        }


        Query albumQuery = new CriteriaQuery(new Criteria("artists.id").is(artistIdStr));
        SearchHits<AlbumDocument> albumHits = elasticsearchOperations.search(albumQuery, AlbumDocument.class);
        if (albumHits.hasSearchHits()) {
            for (SearchHit<AlbumDocument> hit : albumHits.getSearchHits()) {
                AlbumDocument album = hit.getContent();
                boolean modified = false;
                if (album.getArtists() != null) {
                    for (TrackDocument.ArtistInfoInTrack artistInfo : album.getArtists()) { // Используем ArtistInfoInTrack
                        if (artistIdStr.equals(artistInfo.getId()) && !newArtistName.equals(artistInfo.getName())) {
                            artistInfo.setName(newArtistName);
                            modified = true;
                        }
                    }
                }
                if (modified) {
                    album.setSearchTextAll(buildSearchTextForAlbum(album));
                    album.setUpdatedAt(OffsetDateTime.now());
                    albumEsRepository.save(album);
                }
            }
            log.info("Processed artist name update for {} albums related to artist ID: {}", albumHits.getTotalHits(), artistIdStr);
        }
    }

    private void deleteArtistFromTracks(String artistIdToDelete) {
        log.info("Attempting to remove artist ID {} from tracks", artistIdToDelete);
        Query query = new CriteriaQuery(new Criteria("artists.id").is(artistIdToDelete));
        SearchHits<TrackDocument> trackHits = elasticsearchOperations.search(query, TrackDocument.class);
        if (trackHits.hasSearchHits()) {
            List<TrackDocument> tracksToModify = new ArrayList<>();
            for (SearchHit<TrackDocument> hit : trackHits.getSearchHits()) {
                TrackDocument track = hit.getContent();
                if (track.getArtists() != null) {
                    boolean removed = track.getArtists().removeIf(a -> artistIdToDelete.equals(a.getId()));
                    if (removed) {
                        track.setSearchTextAll(buildSearchTextForTrack(track));
                        track.setUpdatedAt(OffsetDateTime.now());
                        tracksToModify.add(track);
                    }
                }
            }
            if (!tracksToModify.isEmpty()) {
                trackEsRepository.saveAll(tracksToModify);
                log.info("Removed artist ID {} from {} tracks", artistIdToDelete, tracksToModify.size());
            }
        }
    }

    private void deleteArtistFromAlbums(String artistIdToDelete) {
        log.info("Attempting to remove artist ID {} from albums", artistIdToDelete);
        Query query = new CriteriaQuery(new Criteria("artists.id").is(artistIdToDelete));
        SearchHits<AlbumDocument> albumHits = elasticsearchOperations.search(query, AlbumDocument.class);
        if (albumHits.hasSearchHits()) {
            List<AlbumDocument> albumsToModify = new ArrayList<>();
            for (SearchHit<AlbumDocument> hit : albumHits.getSearchHits()) {
                AlbumDocument album = hit.getContent();
                if (album.getArtists() != null) {
                    boolean removed = album.getArtists().removeIf(a -> artistIdToDelete.equals(a.getId()));
                    if (removed) {
                        album.setSearchTextAll(buildSearchTextForAlbum(album));
                        album.setUpdatedAt(OffsetDateTime.now());
                        albumsToModify.add(album);
                    }
                }
            }
            if (!albumsToModify.isEmpty()) {
                albumEsRepository.saveAll(albumsToModify);
                log.info("Removed artist ID {} from {} albums", artistIdToDelete, albumsToModify.size());
            }
        }
    }

    private void clearAlbumInfoFromTracks(String albumIdToDelete) {
        log.info("Attempting to clear album info (ID {}) from tracks", albumIdToDelete);
        Query query = new CriteriaQuery(new Criteria("album.id").is(albumIdToDelete));
        SearchHits<TrackDocument> trackHits = elasticsearchOperations.search(query, TrackDocument.class);
        if (trackHits.hasSearchHits()) {
            List<TrackDocument> tracksToModify = new ArrayList<>();
            for (SearchHit<TrackDocument> hit : trackHits.getSearchHits()) {
                TrackDocument track = hit.getContent();
                if (track.getAlbum() != null && albumIdToDelete.equals(track.getAlbum().getId())) {
                    track.setAlbum(null); // Очищаем информацию об альбоме
                    track.setSearchTextAll(buildSearchTextForTrack(track)); // Пересчитываем search_text_all
                    track.setUpdatedAt(OffsetDateTime.now());
                    tracksToModify.add(track);
                }
            }
            if(!tracksToModify.isEmpty()){
                trackEsRepository.saveAll(tracksToModify);
                log.info("Cleared album info from {} tracks for deleted album ID {}", tracksToModify.size(), albumIdToDelete);
            }
        }
    }


    private String buildSearchTextForTrack(TrackDocument doc) {
        StringBuilder sb = new StringBuilder();
        if (doc.getTitle() != null) sb.append(doc.getTitle()).append(" ");
        if (doc.getAlbum() != null && doc.getAlbum().getTitle() != null) sb.append(doc.getAlbum().getTitle()).append(" ");
        if (doc.getArtists() != null) doc.getArtists().forEach(a -> { if (a.getName() != null) sb.append(a.getName()).append(" "); });
        if (doc.getGenres() != null) doc.getGenres().forEach(g -> { if (g.getName() != null) sb.append(g.getName()).append(" "); });
        return sb.toString().trim().toLowerCase();
    }

    private String buildSearchTextForAlbum(AlbumDocument doc) {
        StringBuilder sb = new StringBuilder();
        if (doc.getTitle() != null) sb.append(doc.getTitle()).append(" ");
        if (doc.getArtists() != null) doc.getArtists().forEach(a -> { if (a.getName() != null) sb.append(a.getName()).append(" "); });
        if (doc.getGenres() != null) doc.getGenres().forEach(g -> { if (g.getName() != null) sb.append(g.getName()).append(" "); });
        return sb.toString().trim().toLowerCase();
    }

    private String buildSearchTextForArtist(ArtistDocument doc) {
        StringBuilder sb = new StringBuilder();
        if (doc.getName() != null) sb.append(doc.getName()).append(" ");
        if (doc.getBio() != null) sb.append(doc.getBio()).append(" ");
        if (doc.getTopGenres() != null) doc.getTopGenres().forEach(g -> { if (g.getName() != null) sb.append(g.getName()).append(" "); });
        return sb.toString().trim().toLowerCase();
    }

    // Вспомогательные методы для конвертации UpdatedEvent в CreatedEvent (если документ не найден)
    private TrackCreatedEvent convertTrackUpdatedToCreated(TrackUpdatedEvent updatedEvent) {
        return new TrackCreatedEvent(
                updatedEvent.trackId(), updatedEvent.title(), updatedEvent.durationMs(),
                updatedEvent.audioFileS3Key(), updatedEvent.releaseDate(), updatedEvent.isExplicit(),
                updatedEvent.albumId(), updatedEvent.albumTitle(),
                // Конвертация коллекций, если нужны
                updatedEvent.artists() != null ? updatedEvent.artists().stream().map(a -> new TrackCreatedEvent.ArtistInfo(a.artistId(), a.name())).collect(Collectors.toSet()) : Collections.emptySet(),
                updatedEvent.genres() != null ? updatedEvent.genres().stream().map(g -> new TrackCreatedEvent.GenreInfo(g.genreId(), g.name())).collect(Collectors.toSet()) : Collections.emptySet()
        );
    }
    private AlbumCreatedEvent convertAlbumUpdatedToCreated(AlbumUpdatedEvent updatedEvent) {
        return new AlbumCreatedEvent(
                updatedEvent.albumId(), updatedEvent.title(), updatedEvent.releaseDate(),
                updatedEvent.albumType(), updatedEvent.s3AlbumCoverKey(),
                updatedEvent.artists() != null ? updatedEvent.artists().stream().map(a -> new TrackCreatedEvent.ArtistInfo(a.artistId(), a.name())).collect(Collectors.toSet()) : Collections.emptySet(),
                updatedEvent.genres() != null ? updatedEvent.genres().stream().map(g -> new TrackCreatedEvent.GenreInfo(g.genreId(), g.name())).collect(Collectors.toSet()) : Collections.emptySet()
        );
    }
    private ArtistCreatedEvent convertArtistUpdatedToCreated(ArtistUpdatedEvent updatedEvent) {
        return new ArtistCreatedEvent(
                updatedEvent.artistId(), updatedEvent.name(), updatedEvent.bio(), updatedEvent.s3ArtistPhotoKey()
                // topGenres для нового артиста обычно пустые или вычисляются позже
        );
    }
}