package com.adbroker.redirector.web;

import com.adbroker.redirector.service.LinkService;
import com.adbroker.redirector.service.geo.GeoService;
import com.adbroker.redirector.strategy.CampaignContext;
import com.adbroker.redirector.strategy.StrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final LinkService linkService;
    private final StrategyFactory strategyFactory;
    private final GeoService geoService;

    @GetMapping("/{shortCode}")
    public Mono<ResponseEntity<Void>> redirect(@PathVariable String shortCode, ServerWebExchange exchange) {
        String ipAddress = getIpAddress(exchange.getRequest());
        String userAgent = exchange.getRequest().getHeaders().getFirst("User-Agent");

        return linkService.findRoute(shortCode)
                .filter(route -> "ACTIVE".equals(route.getStatus()))
                .flatMap(route -> {
                    return geoService.resolveCountry(ipAddress)
                            .flatMap(country -> {
                                CampaignContext context = CampaignContext.builder()
                                        .route(route)
                                        .ipAddress(ipAddress)
                                        .userAgent(userAgent)
                                        .country(country)
                                        .build();
                                return strategyFactory.getStrategy(context)
                                        .resolveUrl(context)
                                        .map(targetUrl -> {
                                            log.info("Redirecting {} -> {} (IP: {}, Country: {})",
                                                    shortCode, targetUrl, ipAddress, country);
                                            return ResponseEntity.status(HttpStatus.FOUND)
                                                    .location(URI.create(targetUrl))
                                                    .<Void>build();
                                        });
                            });
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).<Void>build()));
    }

    private String getIpAddress(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddress() != null
                ? request.getRemoteAddress().getAddress().getHostAddress()
                : "unknown";
    }
}