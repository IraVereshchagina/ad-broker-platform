package com.adbroker.manager.web;

import com.adbroker.manager.entities.Campaign;
import com.adbroker.manager.repositories.CampaignRepository;
import com.adbroker.manager.service.CampaignService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CampaignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAndApproveCampaign() throws Exception {
        String jsonRequest = """
                {
                    "title": "Black Friday Sale",
                    "adUrl": "https://shop.com/sale"
                }
                """;

        String responseJson = mockMvc.perform(post("/api/v1/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andReturn().getResponse().getContentAsString();

        Campaign created = objectMapper.readValue(responseJson, Campaign.class);
        String id = created.getId();

        mockMvc.perform(post("/api/v1/campaigns/" + id + "/send-event")
                        .param("event", "SEND_TO_MODERATION"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/campaigns/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("MODERATION"));
    }
}