package com.searchservice.proj.event.catalog;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CatalogEvent<T>(
        UUID eventId,
        String eventType,
        OffsetDateTime eventTimestamp,
        String aggregateId,
        String aggregateType,
        int version,
        T payload
) {}