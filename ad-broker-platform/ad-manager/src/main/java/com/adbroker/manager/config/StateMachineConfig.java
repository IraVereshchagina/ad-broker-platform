package com.adbroker.manager.config;

import com.adbroker.common.entities.CampaignEvent;
import com.adbroker.common.entities.CampaignStatus;
import com.adbroker.manager.repositories.CampaignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;

import java.math.BigDecimal;
import java.util.EnumSet;

@Slf4j
@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<CampaignStatus, CampaignEvent> {

    private final CampaignRepository campaignRepository;

    @Override
    public void configure(StateMachineStateConfigurer<CampaignStatus, CampaignEvent> states) throws Exception {
        states
                .withStates()
                .initial(CampaignStatus.DRAFT)
                .states(EnumSet.allOf(CampaignStatus.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<CampaignStatus, CampaignEvent> transitions) throws Exception {
        transitions
            .withExternal()
                .source(CampaignStatus.DRAFT).target(CampaignStatus.MODERATION)
                .event(CampaignEvent.SEND_TO_MODERATION)
                .and()
            .withExternal()
                .source(CampaignStatus.MODERATION).target(CampaignStatus.ACTIVE)
                .event(CampaignEvent.APPROVE)
                .guard(checkBudgetAndUrl())
                .and()
            .withExternal()
                .source(CampaignStatus.MODERATION).target(CampaignStatus.DRAFT)
                .event(CampaignEvent.DECLINE)
                .and()
            .withExternal()
                .source(CampaignStatus.ACTIVE).target(CampaignStatus.PAUSED)
                .event(CampaignEvent.PAUSE)
                .and()
            .withExternal()
                .source(CampaignStatus.PAUSED).target(CampaignStatus.ACTIVE)
                .event(CampaignEvent.ACTIVATE)
                .guard(checkBudgetAndUrl());
    }

    @Bean
    public Guard<CampaignStatus, CampaignEvent> checkBudgetAndUrl() {
        return context -> {
            String campaignId = (String) context.getMessageHeader("campaign_id");
            if (campaignId == null) return false;

            return campaignRepository.findById(campaignId)
                    .map(campaign -> {
                        boolean hasBudget = campaign.getBudget() != null && campaign.getBudget().compareTo(BigDecimal.ZERO) > 0;
                        boolean hasUrl = campaign.getAdUrl() != null && !campaign.getAdUrl().isBlank();

                        if (!hasBudget || !hasUrl) {
                            log.warn("Guard blocked transition for campaign {}: Budget > 0: {}, URL present: {}", campaignId, hasBudget, hasUrl);
                        }
                        return hasBudget && hasUrl;
                    })
                    .orElse(false);
        };
    }
}