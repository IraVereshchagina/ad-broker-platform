package com.adbroker.stats.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "ad_clicks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClickRecord {

    @Id
    private String id;
    private String campaignId;
    private String shortCode;
    private String ipAddress;
    private String country;
    private String userAgent;
    private Instant clickedAt;

    @Builder.Default
    private LocalDateTime processedAt = LocalDateTime.now();
}