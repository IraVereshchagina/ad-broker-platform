package com.adbroker.redirector.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkRoute {
    private String id;
    private String shortCode;
    private String originalUrl;
    private String status;
}