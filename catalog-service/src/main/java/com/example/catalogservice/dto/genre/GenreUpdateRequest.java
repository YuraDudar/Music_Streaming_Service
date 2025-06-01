package com.example.catalogservice.dto.genre;

import lombok.Builder;

@Builder
public record GenreUpdateRequest(
        String name, // Обычно только имя меняют, но можно и описание
        String description
) {}