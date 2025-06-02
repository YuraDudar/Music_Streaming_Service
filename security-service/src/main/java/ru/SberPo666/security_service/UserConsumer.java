package ru.SberPo666.security_service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class UserConsumer {

    private final JdbcTemplate jdbcTemplate;

    @Data
    class UserEventCreatedOrUpdated{
        private UUID userId;
        private String email;
        private String password;
        private String[] roles;
    }

    @Data
    class UserEventDelete{
        private UUID userId;
    }

    @KafkaListener(topics = "ru.sberpo666.user.created", groupId = "music-streaming")
    @Transactional
    public void consumeCreateTrackEvent(String message) throws JsonProcessingException {
        UserEventCreatedOrUpdated event = new ObjectMapper().readValue(message, UserEventCreatedOrUpdated.class);

        jdbcTemplate.update("""
                insert into users (id, username, email, password, enabled)
                values (?, ?, ?, ?, ?)
                """,
                event.getUserId(),
                event.getEmail(),
                event.getEmail(),
                event.getPassword(),
                true
        );

        for(String role : event.roles){
            jdbcTemplate.update("""
                    insert into authorities (user_id, authority)
                    values (?, ?)
                    """,
                    event.getUserId(),
                    role
            );
        }

    }

    @KafkaListener(topics = "ru.sberpo666.user.updated", groupId = "music-streaming")
    @Transactional
    public void consumeUpdateTrackEvent(String message) throws JsonProcessingException {
        UserEventCreatedOrUpdated event = new ObjectMapper().readValue(message, UserEventCreatedOrUpdated.class);

        jdbcTemplate.update("""
                insert into users (id, username, email, password, enabled)
                values (?, ?, ?, ?, ?)
                """,
                event.getUserId(),
                event.getEmail(),
                event.getEmail(),
                event.getPassword(),
                true
        );

        for(String role : event.roles){
            jdbcTemplate.update("""
                    insert into authorities (user_id, authority)
                    values (?, ?)
                    """,
                    event.getUserId(),
                    role
            );
        }

    }

    @KafkaListener(topics = "ru.sberpo666.user.deleted", groupId = "music-streaming")
    @Transactional
    public void consumeDeleteTrackEvent(String message) throws JsonProcessingException {
        UserEventDelete event = new ObjectMapper().readValue(message, UserEventDelete.class);

        jdbcTemplate.update("""
                delete from users where id = ?
                """,
                event.getUserId()
        );

        jdbcTemplate.update("""
                delete from authorities where user_id = ?
                """,
                event.getUserId()
        );

    }

}
