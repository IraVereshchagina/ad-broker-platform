package com.adbroker.manager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTargetingRuleRequest {
    @NotBlank
    private String attribute;
    @NotBlank
    private String operator;
    @NotBlank
    private String value;
}