package com.example.catalogservice.mapper; // Замените на ваш пакет

import com.example.catalogservice.dto.track.TrackCreateRequest;
import com.example.catalogservice.dto.track.TrackResponse;
import com.example.catalogservice.dto.track.TrackSummaryDto;
import com.example.catalogservice.dto.track.TrackUpdateRequest;
import com.example.catalogservice.entity.TrackEntity;
import com.example.catalogservice.service.S3Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",
        uses = {AlbumMapper.class, ArtistMapper.class, GenreMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class TrackMapper {

    protected S3Service s3Service;

    @Autowired
    public void setS3Service(S3Service s3Service) {
        this.s3Service = s3Service;
    }


    @Mapping(target = "id", ignore = true) // ID генерируется БД
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "album", ignore = true)
    @Mapping(target = "artists", ignore = true)
    @Mapping(target = "genres", ignore = true)
    public abstract TrackEntity toEntity(TrackCreateRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "album", ignore = true)
    @Mapping(target = "artists", ignore = true)
    @Mapping(target = "genres", ignore = true)
    public abstract void updateEntityFromDto(TrackUpdateRequest dto, @MappingTarget TrackEntity entity);

    @Mapping(target = "audioFileS3Url", expression = "java(s3Service != null && entity.getAudioFileS3Key() != null ? s3Service.getObjectUrl(entity.getAudioFileS3Key()) : null)")
    public abstract TrackResponse toResponse(TrackEntity entity);

    @Mapping(target = "audioFileS3Url", expression = "java(s3Service != null && entity.getAudioFileS3Key() != null ? s3Service.getObjectUrl(entity.getAudioFileS3Key()) : null)")
    public abstract TrackSummaryDto toSummaryDto(TrackEntity entity);
}
