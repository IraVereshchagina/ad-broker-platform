package com.adbroker.redirector.kafka;

import com.adbroker.common.events.AdCampaignCreatedEvent;
import com.adbroker.common.events.AdCampaignUpdatedEvent;
import com.adbroker.redirector.model.LinkRoute;
import com.adbroker.redirector.service.LinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(topics = "${app.kafka.campaign-topic}", groupId = "redirector-group")
public class CampaignConsumer {

    private final LinkService linkService;

    @KafkaHandler
    public void handleCreated(AdCampaignCreatedEvent event) {
        log.info("Projecting CREATED event for campaign: {}", event.getCampaignId());

        LinkRoute route = LinkRoute.builder()
                .id(event.getCampaignId())
                .shortCode(event.getShortCode())
                .originalUrl(event.getAdUrl())
                .status(event.getStatus().name())
                .geoTargets(event.getGeoTargets())
                .build();

        linkService.saveRoute(route).subscribe();
    }

    @KafkaHandler
    public void handleUpdated(AdCampaignUpdatedEvent event) {
        log.info("Projecting UPDATED event for campaign: {}", event.getCampaignId());

        LinkRoute route = LinkRoute.builder()
                .id(event.getCampaignId())
                .shortCode(event.getShortCode())
                .originalUrl(event.getAdUrl())
                .status(event.getNewStatus().name())
                .geoTargets(event.getGeoTargets())
                .build();

        linkService.saveRoute(route).subscribe();
    }

    @KafkaHandler(isDefault = true)
    public void handleUnknown(Object object) {
        log.warn("Received unknown event: {}", object);
    }
}