package com.adbroker.manager.service;

import com.adbroker.common.entities.CampaignEvent;
import com.adbroker.common.entities.CampaignStatus;
import com.adbroker.common.events.AdCampaignCreatedEvent;
import com.adbroker.common.events.AdCampaignUpdatedEvent;
import com.adbroker.manager.entities.Campaign;
import com.adbroker.manager.entities.TargetingRule;
import com.adbroker.manager.kafka.CampaignProducer;
import com.adbroker.manager.repositories.CampaignRepository;
import com.adbroker.manager.utils.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final StateMachineFactory<CampaignStatus, CampaignEvent> stateMachineFactory;
    private final CampaignStateChangeInterceptor stateChangeInterceptor;
    private final Base62Encoder base62Encoder;
    private final CampaignProducer campaignProducer;

    @Transactional
    public Campaign createCampaign(String title, String url, String authorId) {
        String shortCode = base62Encoder.generateRandomShortCode();

        Campaign campaign = Campaign.builder()
                .title(title)
                .adUrl(url)
                .authorId(authorId)
                .status(CampaignStatus.DRAFT)
                .budget(BigDecimal.ZERO)
                .shortCode(shortCode)
                .build();

        Campaign saved = campaignRepository.save(campaign);

        AdCampaignCreatedEvent event = AdCampaignCreatedEvent.builder()
                .campaignId(saved.getId())
                .authorId(saved.getAuthorId())
                .adUrl(saved.getAdUrl())
                .shortCode(saved.getShortCode())
                .startTime(Instant.ofEpochSecond(Instant.now().getEpochSecond()))
                .status(saved.getStatus())
                .build();

        campaignProducer.sendCampaignCreated(event);

        return saved;
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

    @Transactional
    public Campaign updateCampaign(String id, String title, String adUrl, BigDecimal budget) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));

        if (title != null) campaign.setTitle(title);
        if (adUrl != null) campaign.setAdUrl(adUrl);
        if (budget != null) campaign.setBudget(budget);

        Campaign saved = campaignRepository.save(campaign);

        AdCampaignUpdatedEvent event = AdCampaignUpdatedEvent.builder()
                .campaignId(saved.getId())
                .newStatus(saved.getStatus())
                .budget(saved.getBudget())
                .adUrl(saved.getAdUrl())
                .shortCode(saved.getShortCode())
                .updatedAt(Instant.now())
                .build();

        campaignProducer.sendCampaignUpdate(event);

        return saved;
    }

    @Transactional
    public void addTargetingRule(String campaignId, String attribute, String operator, String value) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));

        TargetingRule rule = TargetingRule.builder()
                .campaign(campaign)
                .attribute(attribute)
                .operator(operator)
                .ruleValue(value)
                .build();

        campaign.getTargetingRules().add(rule);
        campaignRepository.save(campaign);
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