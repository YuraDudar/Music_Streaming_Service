package com.example.catalogservice.service.impl;

import com.example.catalogservice.dto.track.TrackCreateRequest;
import com.example.catalogservice.dto.track.TrackResponse;
import com.example.catalogservice.dto.track.TrackSummaryDto;
import com.example.catalogservice.dto.track.TrackUpdateRequest;
import com.example.catalogservice.entity.AlbumEntity;
import com.example.catalogservice.entity.ArtistEntity;
import com.example.catalogservice.entity.GenreEntity;
import com.example.catalogservice.entity.TrackEntity;
import com.example.catalogservice.event.track.TrackCreatedEvent;
import com.example.catalogservice.event.track.TrackDeletedEvent;
import com.example.catalogservice.event.track.TrackUpdatedEvent;
import com.example.catalogservice.exception.ResourceNotFoundException;
import com.example.catalogservice.mapper.TrackMapper;
import com.example.catalogservice.repository.AlbumRepository;
import com.example.catalogservice.repository.ArtistRepository;
import com.example.catalogservice.repository.GenreRepository;
import com.example.catalogservice.repository.TrackRepository;
import com.example.catalogservice.service.KafkaEventProducer;
import com.example.catalogservice.service.TrackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackServiceImpl implements TrackService {

    private final TrackRepository trackRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;
    private final TrackMapper trackMapper;
    private final KafkaEventProducer kafkaEventProducer;

    @Override
    @Transactional
    public TrackResponse createTrack(TrackCreateRequest createRequest) {
        log.info("Attempting to create new track with title: {}", createRequest.title());
        TrackEntity trackEntity = trackMapper.toEntity(createRequest);

        if (createRequest.albumId() == null) {
            AlbumEntity newAlbum = new AlbumEntity();
            newAlbum.setTitle(createRequest.title());
            newAlbum.setAlbumType("SINGLE");
            if (createRequest.releaseDate() != null) {
                newAlbum.setReleaseDate(createRequest.releaseDate());
            }
            Set<ArtistEntity> artistsForAlbum = createRequest.artistIds().stream()
                    .map(id -> artistRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Artist not found with id: " + id)))
                    .collect(Collectors.toSet());
            newAlbum.setArtists(artistsForAlbum);
            albumRepository.save(newAlbum);
            trackEntity.setAlbum(newAlbum);
            log.info("Created new single-type album with id: {} for track: {}", newAlbum.getId(), createRequest.title());
        } else {
            AlbumEntity album = albumRepository.findById(createRequest.albumId())
                    .orElseThrow(() -> new ResourceNotFoundException("Album not found with id: " + createRequest.albumId()));
            trackEntity.setAlbum(album);
        }

        Set<ArtistEntity> artists = createRequest.artistIds().stream()
                .map(id -> artistRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Artist not found with id: " + id)))
                .collect(Collectors.toSet());
        trackEntity.setArtists(artists);

        if (createRequest.genreIds() != null && !createRequest.genreIds().isEmpty()) {
            Set<GenreEntity> genres = createRequest.genreIds().stream()
                    .map(id -> genreRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + id)))
                    .collect(Collectors.toSet());
            trackEntity.setGenres(genres);
        } else {
            trackEntity.setGenres(new HashSet<>());
        }

        trackEntity.setIsExplicit(createRequest.isExplicit() != null && createRequest.isExplicit());

        TrackEntity savedTrack = trackRepository.save(trackEntity);
        log.info("Track created successfully with id: {}", savedTrack.getId());

        TrackCreatedEvent eventPayload = new TrackCreatedEvent(
                savedTrack.getId(),
                savedTrack.getTitle(),
                savedTrack.getDurationMs(),
                savedTrack.getAudioFileS3Key(),
                savedTrack.getReleaseDate(),
                savedTrack.getIsExplicit(),
                savedTrack.getAlbum().getId(),
                savedTrack.getAlbum().getTitle(),
                savedTrack.getArtists().stream().map(a -> new TrackCreatedEvent.ArtistInfo(a.getId(), a.getName())).collect(Collectors.toSet()),
                savedTrack.getGenres().stream().map(g -> new TrackCreatedEvent.GenreInfo(g.getId(), g.getName())).collect(Collectors.toSet())
        );
        kafkaEventProducer.sendTrackCreatedEvent(eventPayload);

        return trackMapper.toResponse(savedTrack);
    }

    @Override
    @Transactional(readOnly = true)
    public TrackResponse getTrackById(Long trackId) {
        log.debug("Fetching track by id: {}", trackId);
        TrackEntity trackEntity = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found with id: " + trackId));
        return trackMapper.toResponse(trackEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrackSummaryDto> getAllTracks(Pageable pageable) {
        log.debug("Fetching all tracks, pageable: {}", pageable);
        return trackRepository.findAll(pageable).map(trackMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackSummaryDto> getTracksByAlbumId(Long albumId, Pageable pageable) {
        log.debug("Fetching tracks for album id: {}, pageable: {}", albumId, pageable);
        if (!albumRepository.existsById(albumId)) {
            throw new ResourceNotFoundException("Album not found with id: " + albumId);
        }
        // TODO: Нужен кастомный метод в TrackRepository для поиска по albumId с пагинацией
        // return trackRepository.findByAlbumId(albumId, pageable).stream()
        //        .map(trackMapper::toSummaryDto)
        //        .collect(Collectors.toList());
        // Пока заглушка:
        return trackRepository.findAll(pageable).getContent().stream() // Неправильно, просто для компиляции
                .filter(t -> t.getAlbum().getId().equals(albumId))
                .map(trackMapper::toSummaryDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackSummaryDto> getTracksByArtistId(Long artistId, Pageable pageable) {
        log.debug("Fetching tracks for artist id: {}, pageable: {}", artistId, pageable);
        if (!artistRepository.existsById(artistId)) {
            throw new ResourceNotFoundException("Artist not found with id: " + artistId);
        }
        // TODO: Нужен кастомный метод в TrackRepository для поиска по artistId (через track_artists) с пагинацией
        // return trackRepository.findByArtists_Id(artistId, pageable).stream()
        //        .map(trackMapper::toSummaryDto)
        //        .collect(Collectors.toList());
        // Пока заглушка:
        return trackRepository.findAll(pageable).getContent().stream() // Неправильно, просто для компиляции
                .filter(t -> t.getArtists().stream().anyMatch(a -> a.getId().equals(artistId)))
                .map(trackMapper::toSummaryDto).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public TrackResponse updateTrack(Long trackId, TrackUpdateRequest updateRequest) {
        log.info("Attempting to update track with id: {}", trackId);
        TrackEntity existingTrack = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found with id: " + trackId));

        trackMapper.updateEntityFromDto(updateRequest, existingTrack);

        if (updateRequest.albumId() != null) {
            AlbumEntity album = albumRepository.findById(updateRequest.albumId())
                    .orElseThrow(() -> new ResourceNotFoundException("Album not found with id: " + updateRequest.albumId()));
            existingTrack.setAlbum(album);
        }
        if (updateRequest.artistIds() != null) {
            Set<ArtistEntity> artists = updateRequest.artistIds().stream()
                    .map(id -> artistRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Artist not found with id: " + id)))
                    .collect(Collectors.toSet());
            existingTrack.setArtists(artists);
        }
        if (updateRequest.genreIds() != null) {
            Set<GenreEntity> genres = updateRequest.genreIds().stream()
                    .map(id -> genreRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + id)))
                    .collect(Collectors.toSet());
            existingTrack.setGenres(genres);
        } else if (updateRequest.genreIds() != null && updateRequest.genreIds().isEmpty()) { // Если передан пустой список, очищаем жанры
            existingTrack.setGenres(new HashSet<>());
        }


        TrackEntity updatedTrack = trackRepository.save(existingTrack);
        log.info("Track updated successfully with id: {}", updatedTrack.getId());

        TrackUpdatedEvent eventPayload = new TrackUpdatedEvent(
                updatedTrack.getId(),
                updatedTrack.getTitle(),
                updatedTrack.getDurationMs(),
                updatedTrack.getAudioFileS3Key(),
                updatedTrack.getReleaseDate(),
                updatedTrack.getIsExplicit(),
                updatedTrack.getAlbum().getId(),
                updatedTrack.getAlbum().getTitle(),
                updatedTrack.getArtists().stream().map(a -> new TrackCreatedEvent.ArtistInfo(a.getId(), a.getName())).collect(Collectors.toSet()),
                updatedTrack.getGenres().stream().map(g -> new TrackCreatedEvent.GenreInfo(g.getId(), g.getName())).collect(Collectors.toSet())
        );
        kafkaEventProducer.sendTrackUpdatedEvent(eventPayload);

        return trackMapper.toResponse(updatedTrack);
    }

    @Override
    @Transactional
    public void deleteTrack(Long trackId) {
        log.info("Attempting to delete track with id: {}", trackId);
        TrackEntity trackEntity = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found with id: " + trackId));

        trackRepository.deleteById(trackId);
        log.info("Track deleted successfully with id: {}", trackId);

        TrackDeletedEvent eventPayload = new TrackDeletedEvent(trackId);
        kafkaEventProducer.sendTrackDeletedEvent(eventPayload);
    }
}