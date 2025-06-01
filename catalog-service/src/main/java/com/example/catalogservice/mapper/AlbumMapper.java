package com.example.catalogservice.mapper;

import com.example.catalogservice.dto.album.AlbumCreateRequest;
import com.example.catalogservice.dto.album.AlbumResponse;
import com.example.catalogservice.dto.album.AlbumSummaryDto;
import com.example.catalogservice.dto.album.AlbumUpdateRequest;
import com.example.catalogservice.entity.AlbumEntity;
import com.example.catalogservice.service.S3Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",
        uses = {ArtistMapper.class, GenreMapper.class /*, TrackMapper.class - если бы включали треки */},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class AlbumMapper {

    protected S3Service s3Service;

    @Autowired
    public void setS3Service(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "s3AlbumCoverKey", ignore = true) // Устанавливается отдельно
    @Mapping(target = "artists", ignore = true) // Устанавливается в сервисе
    @Mapping(target = "genres", ignore = true)  // Устанавливается в сервисе
    @Mapping(target = "tracks", ignore = true) // Треки управляются отдельно
    public abstract AlbumEntity toEntity(AlbumCreateRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "s3AlbumCoverKey", ignore = true)
    @Mapping(target = "artists", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "tracks", ignore = true)
    public abstract void updateEntityFromDto(AlbumUpdateRequest dto, @MappingTarget AlbumEntity entity);

    @Mapping(target = "s3AlbumCoverUrl", expression = "java(s3Service != null && entity.getS3AlbumCoverKey() != null ? s3Service.getObjectUrl(entity.getS3AlbumCoverKey()) : null)")
    public abstract AlbumResponse toResponse(AlbumEntity entity);

    @Mapping(target = "s3AlbumCoverUrl", expression = "java(s3Service != null && entity.getS3AlbumCoverKey() != null ? s3Service.getObjectUrl(entity.getS3AlbumCoverKey()) : null)")
    public abstract AlbumSummaryDto toSummaryDto(AlbumEntity entity);
}