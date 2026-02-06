package com.adbroker.redirector.strategy.impl;

import com.adbroker.redirector.strategy.CampaignContext;
import com.adbroker.redirector.strategy.RoutingStrategy;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DefaultRoutingStrategy implements RoutingStrategy {

    @Override
    public Mono<String> resolveUrl(CampaignContext context) {
        return Mono.justOrEmpty(context.getRoute().getOriginalUrl());
    }
}