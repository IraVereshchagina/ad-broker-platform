package com.adbroker.redirector.kafka;

import com.adbroker.common.events.AdClickedEvent;
import com.adbroker.redirector.strategy.CampaignContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClickProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.click-topic}")
    private String topic;

    public void sendClick(CampaignContext context) {
        AdClickedEvent event = AdClickedEvent.builder()
                .id(UUID.randomUUID().toString())
                .campaignId(context.getRoute().getId())
                .shortCode(context.getRoute().getShortCode())
                .ipAddress(context.getIpAddress())
                .country(context.getCountry())
                .userAgent(context.getUserAgent())
                .clickedAt(Instant.now().toEpochMilli())
                .build();

        log.debug("Sending click event for {}", event.getShortCode());

        kafkaTemplate.send(topic, event.getCampaignId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send click event", ex);
                    }
                });
    }
}