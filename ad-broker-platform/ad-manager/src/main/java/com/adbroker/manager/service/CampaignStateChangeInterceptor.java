package com.adbroker.manager.service;

import com.adbroker.common.entities.CampaignEvent;
import com.adbroker.common.entities.CampaignStatus;
import com.adbroker.manager.entities.Campaign;
import com.adbroker.manager.repositories.CampaignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class CampaignStateChangeInterceptor extends StateMachineInterceptorAdapter<CampaignStatus, CampaignEvent> {

    private final CampaignRepository campaignRepository;

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
                    campaignRepository.save(campaign);
                }
            }
        }
    }
}