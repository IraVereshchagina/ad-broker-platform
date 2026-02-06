package com.adbroker.redirector.service.geo.impl;

import com.adbroker.redirector.service.geo.GeoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class MockGeoService implements GeoService {

    @Override
    public Mono<String> resolveCountry(String ipAddress) {
        return Mono.fromSupplier(() -> {
            if (ipAddress == null) return "US";

            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                return "US";
            }

            if (ipAddress.startsWith("77.")) {
                return "RU";
            }

            if (ipAddress.startsWith("46.")) {
                return "DE";
            }

            return "US";
        });
    }
}