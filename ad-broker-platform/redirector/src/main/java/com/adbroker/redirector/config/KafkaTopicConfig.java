package com.adbroker.redirector.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${app.kafka.click-topic}")
    private String clickTopicName;

    @Bean
    public NewTopic clickTopic() {
        return TopicBuilder.name(clickTopicName)
                .partitions(1)
                .replicas(1)
                .build();
    }
}