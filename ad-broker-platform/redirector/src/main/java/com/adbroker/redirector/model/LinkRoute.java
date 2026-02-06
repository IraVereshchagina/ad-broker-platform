package com.adbroker.redirector.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkRoute {
    private String id;
    private String shortCode;
    private String originalUrl;
    private String status;

    @Builder.Default
    private Map<String, String> geoTargets = new HashMap<>();
}