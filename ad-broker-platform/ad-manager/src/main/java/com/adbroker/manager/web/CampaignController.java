package com.adbroker.manager.web;

import com.adbroker.common.entities.CampaignEvent;
import com.adbroker.manager.dto.CreateCampaignRequest;
import com.adbroker.manager.entities.Campaign;
import com.adbroker.manager.repositories.CampaignRepository;
import com.adbroker.manager.service.CampaignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;
    private final CampaignRepository campaignRepository;

    @PostMapping
    public ResponseEntity<Campaign> createCampaign(@RequestBody @Valid CreateCampaignRequest request) {
        String fakeAuthorId = "user-123";

        Campaign campaign = campaignService.createCampaign(
                request.getTitle(),
                request.getAdUrl(),
                fakeAuthorId
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(campaign);
    }

    @GetMapping("/{id}")
    public Campaign getCampaign(@PathVariable String id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaign not found"));
    }

    @PostMapping("/{id}/send-event")
    public ResponseEntity<Void> sendEvent(@PathVariable String id, @RequestParam CampaignEvent event) {
        campaignService.sendEvent(id, event);
        return ResponseEntity.ok().build();
    }
}