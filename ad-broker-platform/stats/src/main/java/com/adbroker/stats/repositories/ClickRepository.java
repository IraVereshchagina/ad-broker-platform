package com.adbroker.stats.repositories;

import com.adbroker.stats.entities.ClickRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClickRepository extends JpaRepository<ClickRecord, String> {
}