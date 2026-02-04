package com.adbroker.manager.config;

import com.adbroker.common.entities.CampaignEvent;
import com.adbroker.common.entities.CampaignStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<CampaignStatus, CampaignEvent> {

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
                .event(CampaignEvent.ACTIVATE);
    }
}