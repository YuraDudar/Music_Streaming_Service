package ru.sberpo666.musicplatform.userservice.event;

import java.util.UUID;

public record UserCreatedEvent(UUID userId,
                               String email,
                               String password,
                               String[] roles) {
}
