package com.adbroker.manager.service;

import com.adbroker.common.entities.CampaignEvent;
import com.adbroker.common.entities.CampaignStatus;
import com.adbroker.common.events.AdCampaignUpdatedEvent;
import com.adbroker.manager.entities.Campaign;
import com.adbroker.manager.kafka.CampaignProducer;
import com.adbroker.manager.repositories.CampaignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class CampaignStateChangeInterceptor extends StateMachineInterceptorAdapter<CampaignStatus, CampaignEvent> {

    private final CampaignRepository campaignRepository;
    private final CampaignProducer campaignProducer;

    @Override
    public void preStateChange(State<CampaignStatus, CampaignEvent> state,
                               Message<CampaignEvent> message,
                               Transition<CampaignStatus, CampaignEvent> transition,
                               StateMachine<CampaignStatus, CampaignEvent> stateMachine,
                               StateMachine<CampaignStatus, CampaignEvent> rootStateMachine) {

        if (message != null) {
            String campaignId = (String) message.getHeaders().get("campaign_id");

            if (campaignId != null) {
                log.info("Transitioning campaign {} to status {}", campaignId, state.getId());

                Optional<Campaign> campaignOpt = campaignRepository.findById(campaignId);
                if (campaignOpt.isPresent()) {
                    Campaign campaign = campaignOpt.get();
                    campaign.setStatus(state.getId());
                    Campaign saved = campaignRepository.save(campaign);

                    AdCampaignUpdatedEvent event = AdCampaignUpdatedEvent.builder()
                            .campaignId(saved.getId())
                            .newStatus(state.getId())
                            .budget(saved.getBudget())
                            .updatedAt(Instant.now())
                            .build();

                    try {
                        campaignProducer.sendCampaignUpdate(event);
                    } catch (Exception e) {
                        log.error("Failed to send Kafka event for campaign {}", campaignId, e);
                    }
                }
            }
        }
    }
}