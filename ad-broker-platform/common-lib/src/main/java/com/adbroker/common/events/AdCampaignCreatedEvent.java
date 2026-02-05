package com.adbroker.common.events;

import com.adbroker.common.entities.CampaignStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdCampaignCreatedEvent {
    private String campaignId;
    private String authorId;
    private String adUrl;
    private Instant startTime;
    private CampaignStatus status;
    private String shortCode;
}