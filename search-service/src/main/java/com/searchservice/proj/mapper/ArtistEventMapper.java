package com.searchservice.proj.mapper;

import com.searchservice.proj.document.ArtistDocument;
import com.searchservice.proj.document.TrackDocument; // Для GenreInfoInTrack
import com.searchservice.proj.event.catalog.artist.ArtistCreatedEvent;
import com.searchservice.proj.event.catalog.artist.ArtistUpdatedEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.OffsetDateTime;
import java.util.Collections;

@Mapper(componentModel = "spring",
        imports = {OffsetDateTime.class, Collections.class,
                TrackDocument.GenreInfoInTrack.class
        },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ArtistEventMapper {

    @Mapping(target = "id", expression = "java(String.valueOf(payload.artistId()))")
    @Mapping(target = "nameSort", expression = "java(payload.name() != null ? payload.name().toLowerCase() : null)")
    @Mapping(target = "topGenres", expression = "java(Collections.emptyList())") // Инициализируем пустым
    @Mapping(target = "searchTextAll", ignore = true)
    @Mapping(target = "popularityScore", constant = "0.0")
    @Mapping(target = "createdAt", expression = "java(OffsetDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(OffsetDateTime.now())")
    ArtistDocument toDocument(ArtistCreatedEvent payload);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nameSort", expression = "java(payload.name() != null ? payload.name().toLowerCase() : null)")
    @Mapping(target = "topGenres", ignore = true) // topGenres обновляются отдельной логикой
    @Mapping(target = "searchTextAll", ignore = true)
    @Mapping(target = "popularityScore", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(OffsetDateTime.now())")
    void updateDocument(ArtistUpdatedEvent payload, @MappingTarget ArtistDocument document);
}