package com.example.catalogservice.service.impl;

import com.example.catalogservice.dto.album.AlbumCreateRequest;
import com.example.catalogservice.dto.album.AlbumResponse;
import com.example.catalogservice.dto.album.AlbumSummaryDto;
import com.example.catalogservice.dto.album.AlbumUpdateRequest;
import com.example.catalogservice.dto.track.TrackSummaryDto;
import com.example.catalogservice.entity.AlbumEntity;
import com.example.catalogservice.entity.ArtistEntity;
import com.example.catalogservice.entity.GenreEntity;
import com.example.catalogservice.event.album.AlbumCreatedEvent;
import com.example.catalogservice.event.album.AlbumDeletedEvent;
import com.example.catalogservice.event.album.AlbumUpdatedEvent;
import com.example.catalogservice.event.track.TrackCreatedEvent;
import com.example.catalogservice.exception.ResourceNotFoundException;
import com.example.catalogservice.mapper.AlbumMapper;
import com.example.catalogservice.mapper.TrackMapper;
import com.example.catalogservice.repository.AlbumRepository;
import com.example.catalogservice.repository.ArtistRepository;
import com.example.catalogservice.repository.GenreRepository;
import com.example.catalogservice.repository.TrackRepository;
import com.example.catalogservice.service.AlbumService;
import com.example.catalogservice.service.KafkaEventProducer;
import com.example.catalogservice.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException; // Для S3Service
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;
    private final TrackRepository trackRepository;
    private final AlbumMapper albumMapper;
    private final TrackMapper trackMapper;
    private final KafkaEventProducer kafkaEventProducer;
    private final S3Service s3Service;

    @Override
    @Transactional
    public AlbumResponse createAlbum(AlbumCreateRequest createRequest) {
        log.info("Creating new album with title: {}", createRequest.title());
        AlbumEntity albumEntity = albumMapper.toEntity(createRequest);

        Set<ArtistEntity> artists = createRequest.artistIds().stream()
                .map(id -> artistRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Artist not found with id: " + id)))
                .collect(Collectors.toSet());
        albumEntity.setArtists(artists);

        if (createRequest.genreIds() != null && !createRequest.genreIds().isEmpty()) {
            Set<GenreEntity> genres = createRequest.genreIds().stream()
                    .map(id -> genreRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + id)))
                    .collect(Collectors.toSet());
            albumEntity.setGenres(genres);
        } else {
            albumEntity.setGenres(new HashSet<>());
        }

        AlbumEntity savedAlbum = albumRepository.save(albumEntity);
        log.info("Album created successfully with id: {}", savedAlbum.getId());

        AlbumCreatedEvent eventPayload = new AlbumCreatedEvent(
                savedAlbum.getId(),
                savedAlbum.getTitle(),
                savedAlbum.getReleaseDate(),
                savedAlbum.getAlbumType(),
                savedAlbum.getS3AlbumCoverKey(),
                savedAlbum.getArtists().stream().map(a -> new TrackCreatedEvent.ArtistInfo(a.getId(), a.getName())).collect(Collectors.toSet()),
                savedAlbum.getGenres().stream().map(g -> new TrackCreatedEvent.GenreInfo(g.getId(), g.getName())).collect(Collectors.toSet())
        );
        kafkaEventProducer.sendAlbumCreatedEvent(eventPayload);

        return albumMapper.toResponse(savedAlbum);
    }

    @Override
    @Transactional(readOnly = true)
    public AlbumResponse getAlbumById(Long albumId) {
        log.debug("Fetching album by id: {}", albumId);
        AlbumEntity albumEntity = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found with id: " + albumId));
        return albumMapper.toResponse(albumEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlbumSummaryDto> getAllAlbums(Pageable pageable) {
        log.debug("Fetching all albums, pageable: {}", pageable);
        return albumRepository.findAll(pageable).map(albumMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AlbumSummaryDto> getAlbumsByArtistId(Long artistId, Pageable pageable) {
        log.debug("Fetching albums for artist id: {}, pageable: {}", artistId, pageable);
        if (!artistRepository.existsById(artistId)) {
            throw new ResourceNotFoundException("Artist not found with id: " + artistId);
        }
        return albumRepository.findByArtists_Id(artistId, pageable).map(albumMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackSummaryDto> getTracksByAlbumId(Long albumId, Pageable pageable) {
        log.debug("Fetching tracks for album id: {}, pageable: {}", albumId, pageable);
        if (!albumRepository.existsById(albumId)) {
            throw new ResourceNotFoundException("Album not found with id: " + albumId);
        }
        // Предполагаем, что в TrackRepository есть метод findByAlbum_Id
        return trackRepository.findByAlbum_Id(albumId, pageable).stream()
                .map(trackMapper::toSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AlbumResponse updateAlbum(Long albumId, AlbumUpdateRequest updateRequest) {
        log.info("Updating album with id: {}", albumId);
        AlbumEntity existingAlbum = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found with id: " + albumId));

        albumMapper.updateEntityFromDto(updateRequest, existingAlbum);

        if (updateRequest.artistIds() != null) {
            Set<ArtistEntity> artists = updateRequest.artistIds().stream()
                    .map(id -> artistRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Artist not found with id: " + id)))
                    .collect(Collectors.toSet());
            existingAlbum.setArtists(artists);
        }
        if (updateRequest.genreIds() != null) {
            Set<GenreEntity> genres = updateRequest.genreIds().stream()
                    .map(id -> genreRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + id)))
                    .collect(Collectors.toSet());
            existingAlbum.setGenres(genres);
        } else if (updateRequest.genreIds() != null && updateRequest.genreIds().isEmpty()) {
            existingAlbum.setGenres(new HashSet<>());
        }

        AlbumEntity updatedAlbum = albumRepository.save(existingAlbum);
        log.info("Album updated successfully with id: {}", updatedAlbum.getId());

        AlbumUpdatedEvent eventPayload = new AlbumUpdatedEvent(
                updatedAlbum.getId(),
                updatedAlbum.getTitle(),
                updatedAlbum.getReleaseDate(),
                updatedAlbum.getAlbumType(),
                updatedAlbum.getS3AlbumCoverKey(),
                updatedAlbum.getArtists().stream().map(a -> new TrackCreatedEvent.ArtistInfo(a.getId(), a.getName())).collect(Collectors.toSet()),
                updatedAlbum.getGenres().stream().map(g -> new TrackCreatedEvent.GenreInfo(g.getId(), g.getName())).collect(Collectors.toSet())
        );
        kafkaEventProducer.sendAlbumUpdatedEvent(eventPayload);

        return albumMapper.toResponse(updatedAlbum);
    }

    @Override
    @Transactional
    public AlbumResponse uploadAlbumCover(Long albumId, MultipartFile coverFile) {
        log.info("Uploading cover for album id: {}", albumId);
        AlbumEntity album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found with id: " + albumId));

        if (coverFile == null || coverFile.isEmpty()) {
            throw new IllegalArgumentException("Cover file cannot be empty.");
        }

        String originalFilename = StringUtils.cleanPath(coverFile.getOriginalFilename());
        String fileExtension = StringUtils.getFilenameExtension(originalFilename);
        String s3Key = "covers/albums/" + albumId + "/" + UUID.randomUUID().toString() + "." + fileExtension;

        album.setS3AlbumCoverKey(s3Key);
        AlbumEntity updatedAlbum = albumRepository.save(album);
        log.info("Album cover key set to: {} for album id: {}", s3Key, albumId);

        AlbumUpdatedEvent eventPayload = new AlbumUpdatedEvent(
                updatedAlbum.getId(), updatedAlbum.getTitle(), updatedAlbum.getReleaseDate(),
                updatedAlbum.getAlbumType(), updatedAlbum.getS3AlbumCoverKey(),
                updatedAlbum.getArtists().stream().map(a -> new TrackCreatedEvent.ArtistInfo(a.getId(), a.getName())).collect(Collectors.toSet()),
                updatedAlbum.getGenres().stream().map(g -> new TrackCreatedEvent.GenreInfo(g.getId(), g.getName())).collect(Collectors.toSet())
        );
        kafkaEventProducer.sendAlbumUpdatedEvent(eventPayload);

        return albumMapper.toResponse(updatedAlbum);
    }

    @Override
    @Transactional
    public void deleteAlbum(Long albumId) {
        log.info("Deleting album with id: {}", albumId);
        if (!albumRepository.existsById(albumId)) {
            throw new ResourceNotFoundException("Album not found with id: " + albumId);
        }
        albumRepository.deleteById(albumId);
        log.info("Album deleted successfully with id: {}", albumId);

        AlbumDeletedEvent eventPayload = new AlbumDeletedEvent(albumId);
        kafkaEventProducer.sendAlbumDeletedEvent(eventPayload);
    }
}