package ru.SberPo666.interaction_service.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "listening_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoryEntity {
    @Id
    private UUID history_id;
    private UUID user_id;
    private UUID track_id;
    private LocalDateTime listened_at;
}
