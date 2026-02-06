package com.adbroker.redirector.service.geo;

import reactor.core.publisher.Mono;

public interface GeoService {
    Mono<String> resolveCountry(String ipAddress);
}