package com.adbroker.manager.repositories;

import com.adbroker.manager.entities.Campaign;
import com.adbroker.common.entities.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, String> {
    List<Campaign> findByAuthorId(String authorId);
    List<Campaign> findByStatus(CampaignStatus status);
}