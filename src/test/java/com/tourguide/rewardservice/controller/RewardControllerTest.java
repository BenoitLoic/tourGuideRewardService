package com.tourguide.rewardservice.controller;

import com.tourguide.rewardservice.service.RewardsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.get;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RewardController.class)
class RewardControllerTest {

    private final String getUserRewards = "/rewards/get";
    private final String addReward = "/rewards/add";
    private final UUID userId = UUID.randomUUID();

    @Autowired
    MockMvc mockMvc;
    @MockBean
    RewardsServiceImpl rewardsService;


    @Test
    void getUserRewardsValid() throws Exception {

        // GIVEN

        // WHEN

        // THEN
        mockMvc
                .perform(MockMvcRequestBuilders.get("/rewards/get")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }


    @Test
    void getUserRewardsInvalid() throws Exception {

        // GIVEN

        // WHEN

        // THEN
        mockMvc
                .perform(MockMvcRequestBuilders.get("/rewards/get")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }


}