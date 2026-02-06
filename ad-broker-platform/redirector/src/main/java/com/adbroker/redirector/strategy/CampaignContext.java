package com.adbroker.redirector.strategy;

import com.adbroker.redirector.model.LinkRoute;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CampaignContext {
    private final LinkRoute route;
    private final String ipAddress;
    private final String userAgent;
    private final String country;
}