package com.adbroker.manager.repositories;

import com.adbroker.manager.entities.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findAllByOrderByCreatedAtAsc();
}