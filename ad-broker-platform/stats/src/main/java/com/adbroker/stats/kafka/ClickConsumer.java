package com.adbroker.stats.kafka;

import com.adbroker.common.events.AdClickedEvent;
import com.adbroker.stats.entities.ClickRecord;
import com.adbroker.stats.repositories.ClickRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClickConsumer {

    private final ClickRepository clickRepository;

    @KafkaListener(topics = "${app.kafka.click-topic}", groupId = "stats-group")
    @Transactional
    public void consumeClick(AdClickedEvent event) {
        log.debug("Saving click for campaign: {}", event.getCampaignId());

        ClickRecord record = ClickRecord.builder()
                .id(event.getId())
                .campaignId(event.getCampaignId())
                .shortCode(event.getShortCode())
                .ipAddress(event.getIpAddress())
                .country(event.getCountry())
                .userAgent(event.getUserAgent())
                .clickedAt(Instant.ofEpochMilli(event.getClickedAt())
                        .atZone(ZoneId.of("UTC"))
                        .toLocalDateTime())
                .build();

        clickRepository.save(record);
    }
}