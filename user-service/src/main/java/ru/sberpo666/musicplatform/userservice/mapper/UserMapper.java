package ru.sberpo666.musicplatform.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.sberpo666.musicplatform.userservice.dto.UserDto;
import ru.sberpo666.musicplatform.userservice.entity.User;

@Mapper(componentModel = "spring", uses = RoleMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "roles", source = "roleIds")
    User toUser(UserDto dto);

    @Mapping(target = "roleIds", source = "roles")
    UserDto toDto(User user);

    @Mapping(target = "roles", source = "roleIds")
    User updateFromDto(UserDto dto, @MappingTarget User user);
}
