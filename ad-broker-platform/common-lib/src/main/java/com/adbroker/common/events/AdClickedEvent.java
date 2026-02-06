package com.adbroker.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdClickedEvent {
    private String id;
    private String campaignId;
    private String shortCode;
    private String ipAddress;
    private String country;
    private String userAgent;
    private long clickedAt;
}