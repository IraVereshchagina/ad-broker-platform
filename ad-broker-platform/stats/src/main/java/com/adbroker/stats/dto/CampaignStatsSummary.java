package com.adbroker.stats.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CampaignStatsSummary {
    private String campaignId;
    private Long totalClicks;
    private List<StatPoint> topCountries;
    private List<StatPoint> clicksByDate;
}