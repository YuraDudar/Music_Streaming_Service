package ru.SberPo666.metric_service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class MetricService {

    private final MeterRegistry meterRegistry;
    private final JdbcTemplate clickhouseJdbcTemplate;

    private final Counter totalEventsConsumed;
    private final ConcurrentHashMap<String, Counter> trackPlaysCounters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Counter> genrePlaysCounters = new ConcurrentHashMap<>();

    public MetricService(MeterRegistry meterRegistry,
                         @Qualifier("clickhouseJdbcTemplate") JdbcTemplate clickhouseJdbcTemplate) {
        this.meterRegistry = meterRegistry;
        this.clickhouseJdbcTemplate = clickhouseJdbcTemplate;

        this.totalEventsConsumed = Counter.builder("metrics_events_consumed_total")
                .description("Total number of metric events consumed from Kafka.")
                .register(meterRegistry);
    }

    private void updatePrometheusMetrics(TrackMetricEvent event) {
        trackPlaysCounters.computeIfAbsent(
                event.getTrackId(),
                trackIdVal -> Counter.builder("metric_track_plays_total")
                        .description("Total plays for a specific track")
                        .tag("track_id", trackIdVal)
                        .tag("track_name", event.getName() != null ? event.getName() : "unknown")
                        .register(meterRegistry)
        ).increment();

        if (event.getGenres() != null) {
            for (String genre : event.getGenres()) {
                if (genre != null && !genre.isBlank()) {
                    genrePlaysCounters.computeIfAbsent(
                            genre,
                            genreName -> Counter.builder("metric_genre_plays_total")
                                    .description("Total plays for a specific genre")
                                    .tag("genre", genreName)
                                    .register(meterRegistry)
                    ).increment();
                }
            }
        }
    }

    public void processAndStoreMetric(TrackMetricEvent event) {
        log.debug("Processing single event for trackId: {}", event.getTrackId());
        updatePrometheusMetrics(event);
        storeEventsBatchInClickHouse(List.of(event));
        totalEventsConsumed.increment();
    }

    public void processAndStoreMetricsBatch(List<TrackMetricEvent> events) {
        if (events == null || events.isEmpty()) {
            return;
        }
        log.info("Processing batch of {} events for ClickHouse and Prometheus", events.size());

        for (TrackMetricEvent event : events) {
            updatePrometheusMetrics(event);
        }

        storeEventsBatchInClickHouse(events);
        totalEventsConsumed.increment(events.size());
    }

    private void storeEventsBatchInClickHouse(List<TrackMetricEvent> events) {
        if (events == null || events.isEmpty()) return;

        String sql = "INSERT INTO default.listened_tracks_raw " +
                "(event_time, processing_time, track_id, track_name, genres, release_date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            long startTime = System.currentTimeMillis();
            clickhouseJdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    TrackMetricEvent event = events.get(i);
                    ps.setTimestamp(1, Timestamp.from(event.getListenedAt().toInstant()));
                    ps.setTimestamp(2, Timestamp.from(event.getCreatedAt().toInstant()));
                    ps.setString(3, event.getTrackId());
                    ps.setString(4, event.getName());

                    String[] genresArray = (event.getGenres() != null)
                            ? event.getGenres().stream().filter(g -> g != null && !g.isBlank()).toArray(String[]::new)
                            : new String[0];
                    Array genresSqlArray = ps.getConnection().createArrayOf("String", genresArray);
                    ps.setArray(5, genresSqlArray);

                    ps.setDate(6, java.sql.Date.valueOf(event.getReleaseDate()));
                }

                @Override
                public int getBatchSize() {
                    return events.size();
                }
            });
            long endTime = System.currentTimeMillis();
            log.info("Successfully stored batch of {} events in ClickHouse. Time taken: {} ms", events.size(), (endTime - startTime));
        } catch (Exception e) {
            log.error("Error storing batch of {} events in ClickHouse. First event example: {}", events.size(), events.get(0).getTrackId(), e);
        }
    }
}
