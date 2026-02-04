package com.adbroker.manager.service;

import com.adbroker.common.entities.CampaignEvent;
import com.adbroker.common.entities.CampaignStatus;
import com.adbroker.manager.entities.Campaign;
import com.adbroker.manager.repositories.CampaignRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CampaignServiceTest {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private CampaignRepository campaignRepository;

    @Test
    @DisplayName("Full Cycle Test: Create -> Submit -> Approve -> Activate")
    void testCampaignLifecycle() {
        Campaign campaign = campaignService.createCampaign("Holiday Promo", "http://site.com", "user-1");
        assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.DRAFT);

        campaignService.sendEvent(campaign.getId(), CampaignEvent.SEND_TO_MODERATION);

        Campaign afterSubmit = campaignRepository.findById(campaign.getId()).get();
        assertThat(afterSubmit.getStatus()).isEqualTo(CampaignStatus.MODERATION);

        campaignService.sendEvent(campaign.getId(), CampaignEvent.APPROVE);

        Campaign afterApprove = campaignRepository.findById(campaign.getId()).get();
        assertThat(afterApprove.getStatus()).isEqualTo(CampaignStatus.ACTIVE);
    }
}