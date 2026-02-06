package com.adbroker.redirector.strategy;

import com.adbroker.redirector.strategy.impl.DefaultRoutingStrategy;
import com.adbroker.redirector.strategy.impl.GeoTargetingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StrategyFactory {

    private final DefaultRoutingStrategy defaultStrategy;
    private final GeoTargetingStrategy geoStrategy;

    public RoutingStrategy getStrategy(CampaignContext context) {
        if (context.getRoute().getGeoTargets() != null && !context.getRoute().getGeoTargets().isEmpty()) {
            return geoStrategy;
        }

        return defaultStrategy;
    }
}