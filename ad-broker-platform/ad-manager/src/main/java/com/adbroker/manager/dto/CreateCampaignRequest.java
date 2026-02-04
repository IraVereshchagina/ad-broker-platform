package com.adbroker.manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateCampaignRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "URL is required")
    @Pattern(regexp = "^(http|https)://.*", message = "URL must start with http or https")
    private String adUrl;
}