package com.adbroker.common.events;

import com.adbroker.common.entities.CampaignStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdCampaignUpdatedEvent {
    private String campaignId;
    private CampaignStatus newStatus;
    private BigDecimal budget;
    private Instant updatedAt;
    private String adUrl;
    private String shortCode;
    private Map<String, String> geoTargets;
}