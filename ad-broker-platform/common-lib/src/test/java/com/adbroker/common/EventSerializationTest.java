package com.adbroker.common;

import com.adbroker.common.entities.CampaignStatus;
import com.adbroker.common.events.AdCampaignCreatedEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EventSerializationTest {

    @Test
    void testAdCampaignCreatedEventBuilder() {
        String id = "123";
        String url = "https://google.com";

        AdCampaignCreatedEvent event = AdCampaignCreatedEvent.builder()
                .campaignId(id)
                .adUrl(url)
                .status(CampaignStatus.DRAFT)
                .build();

        Assertions.assertEquals(id, event.getCampaignId());
        Assertions.assertEquals(CampaignStatus.DRAFT, event.getStatus());
        Assertions.assertNotNull(event.toString(), "ToString should not be null");
    }
}