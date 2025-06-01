package ru.SberPo666.interaction_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.SberPo666.interaction_service.Entity.HistoryEntity;

import java.util.List;
import java.util.UUID;

public interface HistoryRepository extends JpaRepository<HistoryEntity, UUID> {

    @Query(value =
            """
            select track_id from listening_history where user_id = :userId order by listened_at desc
            """,
    nativeQuery = true)
    List<UUID> getHistoryByUserId(UUID userId);

}
