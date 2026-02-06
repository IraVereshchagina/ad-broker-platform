package com.adbroker.stats.web;

import com.adbroker.stats.dto.CampaignStatsSummary;
import com.adbroker.stats.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/campaigns/{id}")
    public ResponseEntity<CampaignStatsSummary> getStats(@PathVariable String id) {
        return ResponseEntity.ok(analyticsService.getCampaignStats(id));
    }
}