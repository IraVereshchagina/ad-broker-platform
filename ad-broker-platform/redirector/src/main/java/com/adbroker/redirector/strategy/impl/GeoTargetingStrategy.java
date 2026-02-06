package com.adbroker.redirector.strategy.impl;

import com.adbroker.redirector.strategy.CampaignContext;
import com.adbroker.redirector.strategy.RoutingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
public class GeoTargetingStrategy implements RoutingStrategy {

    @Override
    public Mono<String> resolveUrl(CampaignContext context) {
        String country = context.getCountry();
        Map<String, String> targets = context.getRoute().getGeoTargets();

        if (targets != null && country != null && targets.containsKey(country)) {
            String specificUrl = targets.get(country);
            log.info("GeoTargeting applied: {} -> {}", country, specificUrl);
            return Mono.just(specificUrl);
        }

        return Mono.just(context.getRoute().getOriginalUrl());
    }
}