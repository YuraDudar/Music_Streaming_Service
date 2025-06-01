package ru.SberPo666.interaction_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.SberPo666.interaction_service.Entity.HistoryEntity;

import java.util.UUID;

public interface HistoryRepository extends JpaRepository<HistoryEntity, UUID> {
}
