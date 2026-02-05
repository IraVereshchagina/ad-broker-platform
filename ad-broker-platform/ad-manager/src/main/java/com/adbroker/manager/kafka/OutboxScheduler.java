package com.adbroker.manager.kafka;

import com.adbroker.manager.entities.OutboxEvent;
import com.adbroker.manager.repositories.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Scheduled(fixedDelayString = "${app.kafka.outbox-delay}")
    @Transactional
    public void processOutbox() {
        List<OutboxEvent> events = outboxRepository.findAllByOrderByCreatedAtAsc();

        if (events.isEmpty()) return;

        log.debug("Found {} events in outbox. Processing...", events.size());

        for (OutboxEvent event : events) {
            try {
                ProducerRecord<String, Object> record = new ProducerRecord<>(
                        event.getTopic(),
                        event.getAggregateId(),
                        event.getPayload()
                );

                record.headers().add(new RecordHeader("event_type", event.getEventType().getBytes(StandardCharsets.UTF_8)));

                CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(record);
                SendResult<String, Object> result = future.get(3, TimeUnit.SECONDS);

                log.info("Successfully sent event {} to topic {}, offset {}",
                        event.getId(), event.getTopic(), result.getRecordMetadata().offset());

                outboxRepository.delete(event);

            } catch (Exception e) {
                log.error("Failed to send event {}. Attempt: {}", event.getId(), event.getAttempts(), e);

                event.setAttempts(event.getAttempts() + 1);

                outboxRepository.save(event);
            }
        }
    }
}