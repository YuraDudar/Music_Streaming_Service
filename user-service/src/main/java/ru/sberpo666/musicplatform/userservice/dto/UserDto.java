package ru.sberpo666.musicplatform.userservice.dto;

import javax.validation.constraints.Email;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UserDto(
        UUID userId,
        @Email String email,
        String passwordHash,
        String displayName,
        Integer[] roleIds,
        Boolean isActive,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

}
