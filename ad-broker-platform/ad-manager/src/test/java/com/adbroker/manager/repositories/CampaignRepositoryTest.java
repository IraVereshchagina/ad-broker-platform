package com.adbroker.manager.repositories;

import com.adbroker.common.entities.CampaignStatus;
import com.adbroker.manager.entities.Campaign;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CampaignRepositoryTest {

    @Autowired
    private CampaignRepository campaignRepository;

    @Test
    @DisplayName("Should save and find campaign by ID")
    void shouldSaveAndFindCampaign() {
        Campaign campaign = Campaign.builder()
                .title("Super Sale")
                .authorId("user-1")
                .adUrl("https://example.com")
                .status(CampaignStatus.DRAFT)
                .budget(BigDecimal.TEN)
                .build();

        Campaign saved = campaignRepository.saveAndFlush(campaign);

        Optional<Campaign> found = campaignRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isNotNull();
        assertThat(found.get().getTitle()).isEqualTo("Super Sale");
        assertThat(found.get().getCreatedAt()).isNotNull();
    }
}