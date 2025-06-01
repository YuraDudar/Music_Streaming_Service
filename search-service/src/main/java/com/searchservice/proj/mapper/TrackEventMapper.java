package com.searchservice.proj.mapper;

import com.searchservice.proj.document.TrackDocument;
import com.searchservice.proj.event.catalog.track.TrackCreatedEvent;
import com.searchservice.proj.event.catalog.track.TrackUpdatedEvent;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        imports = {OffsetDateTime.class, Collectors.class, Collections.class,
                TrackDocument.AlbumInfoInTrack.class,
                TrackDocument.ArtistInfoInTrack.class,
                TrackDocument.GenreInfoInTrack.class
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TrackEventMapper {

    @Mapping(target = "id", expression = "java(String.valueOf(payload.trackId()))")
    @Mapping(target = "titleSort", expression = "java(payload.title() != null ? payload.title().toLowerCase() : null)")
    // Если в payload.albumTitle() может быть null, нужна проверка
    @Mapping(target = "album", expression = "java(payload.albumId() != null && payload.albumTitle() != null ? TrackDocument.AlbumInfoInTrack.builder().id(String.valueOf(payload.albumId())).title(payload.albumTitle()).s3CoverKey(null).build() : null)") // s3CoverKey из события трека не берем
    @Mapping(target = "artists", expression = "java(payload.artists() != null ? payload.artists().stream().map(a -> TrackDocument.ArtistInfoInTrack.builder().id(String.valueOf(a.artistId())).name(a.name()).build()).collect(Collectors.toList()) : Collections.emptyList())")
    @Mapping(target = "genres", expression = "java(payload.genres() != null ? payload.genres().stream().map(g -> TrackDocument.GenreInfoInTrack.builder().id(String.valueOf(g.genreId())).name(g.name()).build()).collect(Collectors.toList()) : Collections.emptyList())")
    @Mapping(target = "searchTextAll", ignore = true)
    @Mapping(target = "popularityScore", constant = "0.0")
    @Mapping(target = "playCount", constant = "0L")
    @Mapping(target = "createdAt", expression = "java(OffsetDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(OffsetDateTime.now())")
    TrackDocument toDocument(TrackCreatedEvent payload);


    @Mapping(target = "id", ignore = true) // ID не меняем при обновлении из события
    @Mapping(target = "titleSort", expression = "java(payload.title() != null ? payload.title().toLowerCase() : null)")
    @Mapping(target = "album", expression = "java(payload.albumId() != null && payload.albumTitle() != null ? TrackDocument.AlbumInfoInTrack.builder().id(String.valueOf(payload.albumId())).title(payload.albumTitle()).s3CoverKey(document.getAlbum() != null ? document.getAlbum().getS3CoverKey() : null).build() : document.getAlbum())") // Сохраняем s3CoverKey если есть
    @Mapping(target = "artists", expression = "java(payload.artists() != null ? payload.artists().stream().map(a -> TrackDocument.ArtistInfoInTrack.builder().id(String.valueOf(a.artistId())).name(a.name()).build()).collect(Collectors.toList()) : document.getArtists())")
    @Mapping(target = "genres", expression = "java(payload.genres() != null ? payload.genres().stream().map(g -> TrackDocument.GenreInfoInTrack.builder().id(String.valueOf(g.genreId())).name(g.name()).build()).collect(Collectors.toList()) : document.getGenres())")
    @Mapping(target = "searchTextAll", ignore = true)
    @Mapping(target = "popularityScore", ignore = true)
    @Mapping(target = "playCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(OffsetDateTime.now())")
    void updateDocument(TrackUpdatedEvent payload, @MappingTarget TrackDocument document);
}