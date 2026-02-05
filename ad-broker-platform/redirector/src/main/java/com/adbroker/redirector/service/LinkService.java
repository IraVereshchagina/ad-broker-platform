package com.adbroker.redirector.service;

import com.adbroker.redirector.model.LinkRoute;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkService {

    private final ReactiveRedisTemplate<String, LinkRoute> redisTemplate;

    public Mono<Boolean> saveRoute(LinkRoute route) {
        String key = "link:" + route.getShortCode();
        log.info("Updating Redis route: key={} -> url={}", key, route.getOriginalUrl());
        return redisTemplate.opsForValue().set(key, route);
    }

    public Mono<LinkRoute> findRoute(String shortCode) {
        return redisTemplate.opsForValue().get("link:" + shortCode);
    }
}