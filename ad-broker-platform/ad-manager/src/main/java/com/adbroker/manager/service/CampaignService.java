package com.adbroker.manager.service;

import com.adbroker.common.entities.CampaignEvent;
import com.adbroker.common.entities.CampaignStatus;
import com.adbroker.manager.entities.Campaign;
import com.adbroker.manager.repositories.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final StateMachineFactory<CampaignStatus, CampaignEvent> stateMachineFactory;
    private final CampaignStateChangeInterceptor stateChangeInterceptor;

    @Transactional
    public Campaign createCampaign(String title, String url, String authorId) {
        Campaign campaign = Campaign.builder()
                .title(title)
                .adUrl(url)
                .authorId(authorId)
                .status(CampaignStatus.DRAFT)
                .budget(BigDecimal.ZERO)
                .build();

        return campaignRepository.save(campaign);
    }

    @Transactional
    public void sendEvent(String campaignId, CampaignEvent event) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));

        StateMachine<CampaignStatus, CampaignEvent> sm = build(campaign);

        sm.sendEvent(Mono.just(MessageBuilder.withPayload(event)
                        .setHeader("campaign_id", campaignId)
                        .build()))
                .subscribe();
    }

    private StateMachine<CampaignStatus, CampaignEvent> build(Campaign campaign) {
        StateMachine<CampaignStatus, CampaignEvent> sm = stateMachineFactory.getStateMachine(campaign.getId());
        sm.stopReactively().block();

        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(stateChangeInterceptor);

                    sma.resetStateMachineReactively(
                            new DefaultStateMachineContext<>(
                                    campaign.getStatus(), null, null, null
                            )
                    ).block();
                });

        sm.startReactively().block();
        return sm;
    }
}