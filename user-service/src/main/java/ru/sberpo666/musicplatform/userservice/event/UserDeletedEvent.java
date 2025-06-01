package ru.sberpo666.musicplatform.userservice.event;

import java.util.UUID;

public record UserDeletedEvent(
        UUID userId
) {
}
