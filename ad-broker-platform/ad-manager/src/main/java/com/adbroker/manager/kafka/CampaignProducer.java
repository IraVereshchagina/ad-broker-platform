package com.adbroker.manager.kafka;

import com.adbroker.common.events.AdCampaignCreatedEvent;
import com.adbroker.common.events.AdCampaignUpdatedEvent;
import com.adbroker.manager.entities.OutboxEvent;
import com.adbroker.manager.repositories.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignProducer {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.campaign-topic}")
    private String topic;

    @Transactional
    @SneakyThrows
    public void sendCampaignCreated(AdCampaignCreatedEvent event) {
        saveToOutbox(event.getCampaignId(), "CREATED", event);
    }

    @Transactional
    @SneakyThrows
    public void sendCampaignUpdate(AdCampaignUpdatedEvent event) {
        saveToOutbox(event.getCampaignId(), "UPDATED", event);
    }

    private void saveToOutbox(String aggregateId, String type, Object event) throws Exception {
        String payload = objectMapper.writeValueAsString(event);

        OutboxEvent outbox = OutboxEvent.builder()
                .id(UUID.randomUUID())
                .aggregateId(aggregateId)
                .eventType(type)
                .topic(topic)
                .payload(payload)
                .createdAt(Instant.now())
                .build();

        outboxRepository.save(outbox);
        log.info("Saved {} event to Outbox for campaign {}", type, aggregateId);
    }
}