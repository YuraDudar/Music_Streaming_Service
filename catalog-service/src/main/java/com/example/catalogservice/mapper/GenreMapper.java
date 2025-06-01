package com.example.catalogservice.mapper;

import com.example.catalogservice.dto.genre.GenreCreateRequest;
import com.example.catalogservice.dto.genre.GenreResponse;
import com.example.catalogservice.dto.genre.GenreSummaryDto;
import com.example.catalogservice.dto.genre.GenreUpdateRequest;
import com.example.catalogservice.entity.GenreEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class GenreMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tracks", ignore = true) // Связи управляются не здесь
    @Mapping(target = "albums", ignore = true) // Связи управляются не здесь
    public abstract GenreEntity toEntity(GenreCreateRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tracks", ignore = true)
    @Mapping(target = "albums", ignore = true)
    public abstract void updateEntityFromDto(GenreUpdateRequest dto, @MappingTarget GenreEntity entity);

    public abstract GenreResponse toResponse(GenreEntity entity);

    public abstract GenreSummaryDto toSummaryDto(GenreEntity entity);
}