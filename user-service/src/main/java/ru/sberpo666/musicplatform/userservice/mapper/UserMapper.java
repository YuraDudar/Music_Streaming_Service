package ru.sberpo666.musicplatform.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberpo666.musicplatform.userservice.dto.UserDto;
import ru.sberpo666.musicplatform.userservice.entity.User;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    UserDto toDto(User user);
    User toUser(UserDto dto);
    User updateFromDto(UserDto dto, @MappingTarget User user);
}
