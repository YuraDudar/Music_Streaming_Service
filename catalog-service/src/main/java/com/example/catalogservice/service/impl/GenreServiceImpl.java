package com.example.catalogservice.service.impl;

import com.example.catalogservice.dto.genre.GenreCreateRequest;
import com.example.catalogservice.dto.genre.GenreResponse;
import com.example.catalogservice.dto.genre.GenreUpdateRequest;
import com.example.catalogservice.entity.GenreEntity;
import com.example.catalogservice.event.genre.GenreCreatedEvent;
import com.example.catalogservice.event.genre.GenreDeletedEvent;
import com.example.catalogservice.event.genre.GenreUpdatedEvent;
import com.example.catalogservice.exception.ResourceNotFoundException;
import com.example.catalogservice.mapper.GenreMapper;
import com.example.catalogservice.repository.GenreRepository;
import com.example.catalogservice.service.GenreService;
import com.example.catalogservice.service.KafkaEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;
    private final KafkaEventProducer kafkaEventProducer;

    @Override
    @Transactional
    public GenreResponse createGenre(GenreCreateRequest createRequest) {
        log.info("Creating new genre with name: {}", createRequest.name());


        GenreEntity genreEntity = genreMapper.toEntity(createRequest);
        GenreEntity savedGenre = genreRepository.save(genreEntity);
        log.info("Genre created successfully with id: {}", savedGenre.getId());

        GenreCreatedEvent eventPayload = new GenreCreatedEvent(
                savedGenre.getId(),
                savedGenre.getName(),
                savedGenre.getDescription()
        );
        kafkaEventProducer.sendGenreCreatedEvent(eventPayload);

        return genreMapper.toResponse(savedGenre);
    }

    @Override
    @Transactional(readOnly = true)
    public GenreResponse getGenreById(Integer genreId) {
        log.debug("Fetching genre by id: {}", genreId);
        GenreEntity genreEntity = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + genreId));
        return genreMapper.toResponse(genreEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GenreResponse> getAllGenres() {
        log.debug("Fetching all genres");
        return genreRepository.findAll().stream()
                .map(genreMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GenreResponse updateGenre(Integer genreId, GenreUpdateRequest updateRequest) {
        log.info("Updating genre with id: {}", genreId);
        GenreEntity existingGenre = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + genreId));


        genreMapper.updateEntityFromDto(updateRequest, existingGenre);
        GenreEntity updatedGenre = genreRepository.save(existingGenre);
        log.info("Genre updated successfully with id: {}", updatedGenre.getId());

        GenreUpdatedEvent eventPayload = new GenreUpdatedEvent(
                updatedGenre.getId(),
                updatedGenre.getName(),
                updatedGenre.getDescription()
        );
        kafkaEventProducer.sendGenreUpdatedEvent(eventPayload);

        return genreMapper.toResponse(updatedGenre);
    }

    @Override
    @Transactional
    public void deleteGenre(Integer genreId) {
        log.info("Deleting genre with id: {}", genreId);
        GenreEntity genreEntity = genreRepository.findById(genreId)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id: " + genreId));


        genreRepository.deleteById(genreId); // Физическое удаление
        log.info("Genre deleted successfully with id: {}", genreId);

        GenreDeletedEvent eventPayload = new GenreDeletedEvent(genreId);
        kafkaEventProducer.sendGenreDeletedEvent(eventPayload);
    }
}