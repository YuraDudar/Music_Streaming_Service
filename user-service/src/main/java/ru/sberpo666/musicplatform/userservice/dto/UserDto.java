package ru.sberpo666.musicplatform.userservice.dto;

import javax.validation.constraints.Email;
import java.time.OffsetDateTime;

public record UserDto(
        Long userId,
        @Email String email,
        String passwordHash,
        String displayName,
        Boolean isActive,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

}
