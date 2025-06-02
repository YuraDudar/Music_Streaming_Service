package com.searchservice.proj.mapper;

import com.searchservice.proj.document.AlbumDocument;
import com.searchservice.proj.document.TrackDocument; // Для ArtistInfoInTrack, GenreInfoInTrack
import com.searchservice.proj.event.catalog.album.AlbumCreatedEvent;
import com.searchservice.proj.event.catalog.album.AlbumUpdatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        imports = {OffsetDateTime.class, Collectors.class, Collections.class,
                TrackDocument.ArtistInfoInTrack.class,
                TrackDocument.GenreInfoInTrack.class
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AlbumEventMapper {

    @Mapping(target = "id", expression = "java(String.valueOf(payload.albumId()))")
    @Mapping(target = "titleSort", expression = "java(payload.title() != null ? payload.title().toLowerCase() : null)")
    @Mapping(target = "artists", expression = "java(payload.artists() != null ? payload.artists().stream().map(a -> TrackDocument.ArtistInfoInTrack.builder().id(String.valueOf(a.artistId())).name(a.name()).build()).collect(Collectors.toList()) : Collections.emptyList())")
    @Mapping(target = "genres", expression = "java(payload.genres() != null ? payload.genres().stream().map(g -> TrackDocument.GenreInfoInTrack.builder().id(String.valueOf(g.genreId())).name(g.name()).build()).collect(Collectors.toList()) : Collections.emptyList())")
    @Mapping(target = "searchTextAll", ignore = true)
    @Mapping(target = "popularityScore", constant = "0.0")
    @Mapping(target = "trackCount", constant = "0") // Изначально 0, будет обновляться отдельно
    @Mapping(target = "createdAt", expression = "java(OffsetDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(OffsetDateTime.now())")
    AlbumDocument toDocument(AlbumCreatedEvent payload);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "titleSort", expression = "java(payload.title() != null ? payload.title().toLowerCase() : null)")
    @Mapping(target = "artists", expression = "java(payload.artists() != null ? payload.artists().stream().map(a -> TrackDocument.ArtistInfoInTrack.builder().id(String.valueOf(a.artistId())).name(a.name()).build()).collect(Collectors.toList()) : document.getArtists())")
    @Mapping(target = "genres", expression = "java(payload.genres() != null ? payload.genres().stream().map(g -> TrackDocument.GenreInfoInTrack.builder().id(String.valueOf(g.genreId())).name(g.name()).build()).collect(Collectors.toList()) : document.getGenres())")
    @Mapping(target = "searchTextAll", ignore = true)
    @Mapping(target = "popularityScore", ignore = true)
    @Mapping(target = "trackCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(OffsetDateTime.now())")
    void updateDocument(AlbumUpdatedEvent payload, @MappingTarget AlbumDocument document);
}