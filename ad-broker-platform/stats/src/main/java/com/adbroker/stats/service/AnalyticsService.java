package com.adbroker.stats.service;

import com.adbroker.stats.dto.CampaignStatsSummary;
import com.adbroker.stats.dto.StatPoint;
import com.adbroker.stats.repositories.ClickRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ClickRepository clickRepository;

    @Transactional(readOnly = true)
    public CampaignStatsSummary getCampaignStats(String campaignId) {
        Long total = clickRepository.countByCampaignId(campaignId);
        List<StatPoint> byCountry = clickRepository.countClicksByCountry(campaignId);

        List<Object[]> byHourRaw = clickRepository.countClicksByHour(campaignId);

        List<StatPoint> byHour = byHourRaw.stream()
                .map(row -> new StatPoint(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .toList();

        if (byCountry.size() > 10) {
            byCountry = byCountry.subList(0, 10);
        }

        return CampaignStatsSummary.builder()
                .campaignId(campaignId)
                .totalClicks(total)
                .topCountries(byCountry)
                .clicksByDate(byHour)
                .build();
    }
}