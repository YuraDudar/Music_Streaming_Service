package com.example.catalogservice.mapper;

import com.example.catalogservice.dto.artist.*;
import com.example.catalogservice.entity.ArtistEntity;
import com.example.catalogservice.service.S3Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class ArtistMapper {

    protected S3Service s3Service;

    @Autowired
    public void setS3Service(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "s3ArtistPhotoKey", ignore = true) // Устанавливается отдельно
    @Mapping(target = "tracks", ignore = true) // Связи управляются не здесь
    @Mapping(target = "albums", ignore = true) // Связи управляются не здесь
    public abstract ArtistEntity toEntity(ArtistCreateRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "s3ArtistPhotoKey", ignore = true)
    @Mapping(target = "tracks", ignore = true)
    @Mapping(target = "albums", ignore = true)
    public abstract void updateEntityFromDto(ArtistUpdateRequest dto, @MappingTarget ArtistEntity entity);

    @Mapping(target = "s3ArtistPhotoUrl", expression = "java(s3Service != null && entity.getS3ArtistPhotoKey() != null ? s3Service.getObjectUrl(entity.getS3ArtistPhotoKey()) : null)")
    public abstract ArtistResponse toResponse(ArtistEntity entity);

    @Mapping(target = "s3ArtistPhotoUrl", expression = "java(s3Service != null && entity.getS3ArtistPhotoKey() != null ? s3Service.getObjectUrl(entity.getS3ArtistPhotoKey()) : null)")
    public abstract ArtistSummaryDto toSummaryDto(ArtistEntity entity);

    // Для ArtistMicroDto (обычно только id и name, без S3 URL)
    public abstract ArtistMicroDto toMicroDto(ArtistEntity entity);
}