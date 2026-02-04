package com.adbroker.manager.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateCampaignRequest {
    private String title;
    private String adUrl;
    private BigDecimal budget;
}