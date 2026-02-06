package com.adbroker.redirector.strategy;

import reactor.core.publisher.Mono;

public interface RoutingStrategy {
    Mono<String> resolveUrl(CampaignContext context);

    //String getType();
}