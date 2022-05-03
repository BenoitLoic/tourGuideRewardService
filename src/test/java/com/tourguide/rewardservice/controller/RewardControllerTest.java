package com.tourguide.rewardservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourguide.rewardservice.exception.DataAlreadyExistException;
import com.tourguide.rewardservice.exception.ResourceNotFoundException;
import com.tourguide.rewardservice.model.Attraction;
import com.tourguide.rewardservice.model.Location;
import com.tourguide.rewardservice.model.UserReward;
import com.tourguide.rewardservice.model.VisitedLocation;
import com.tourguide.rewardservice.service.RewardsService;
import com.tourguide.rewardservice.service.RewardsServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RewardController.class)
class RewardControllerTest {

    private final String getUserRewardsUrl = "/rewards/get";
    private final String addRewardUrl = "/rewards/add";
    private final UUID userId = UUID.randomUUID();


    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    RewardsService rewardsServiceMock;

    @BeforeAll
    static void beforeAll() {

    }

    @Test
    void getUserRewardsValid() throws Exception {

        // GIVEN
        UUID attractionId1 = UUID.randomUUID();
        UserReward userReward1 = new UserReward(
                userId,
                new VisitedLocation(userId, new Location(56, 25), new Date()),
                new Attraction("attractionNameTest1", "cityTest", "stateTest", attractionId1, new Location(50, 20)));

        UUID attractionId2 = UUID.randomUUID();
        UserReward userReward2 = new UserReward(
                userId,
                new VisitedLocation(userId, new Location(50, 28), new Date()),
                new Attraction("attractionNameTest2", "cityTest", "stateTest", attractionId2, new Location(50, 20)));

        Collection<UserReward> rewards = new ArrayList<>();
        rewards.add(userReward1);
        rewards.add(userReward1);
        // WHEN
        when(rewardsServiceMock.getUserRewards(userId)).thenReturn(rewards);
        // THEN
        mockMvc
                .perform(get(getUserRewardsUrl)
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

    }


    @Test
    void getUserRewardsInvalid() throws Exception {

        // GIVEN

        // WHEN

        // THEN
        mockMvc
                .perform(get(getUserRewardsUrl)
                        .param("userId", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingServletRequestParameterException));

    }

    @Test
    void getUserRewards_WhenUserDoesntExist_ShouldReturnEmptyList() throws Exception {

        // GIVEN

        // WHEN
        when(rewardsServiceMock.getUserRewards(any())).thenReturn(new ArrayList<>());
        // THEN
        mockMvc
                .perform(get(getUserRewardsUrl)
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals("[]", result.getResponse().getContentAsString()));

    }

    @Test
    void addUserRewardValid() throws Exception {

        // GIVEN
//   UserReward addUserReward( @RequestParam UUID userId, @RequestBody VisitedLocation visitedLocation);

        VisitedLocation visitedLocationTest = new VisitedLocation(userId, new Location(56, 25), new Date());
        String jsonBody = objectMapper.writeValueAsString(visitedLocationTest);

        UUID attractionId1 = UUID.randomUUID();
        UserReward userReward1 = new UserReward(
                userId,
                new VisitedLocation(userId, new Location(56, 25), new Date()),
                new Attraction("attractionNameTest1", "cityTest", "stateTest", attractionId1, new Location(50, 20)));

        String jsonResponse = objectMapper.writeValueAsString(userReward1);
        // WHEN
        when(rewardsServiceMock.addReward(any(), any())).thenReturn(userReward1);
        // THEN
        mockMvc.perform(post(addRewardUrl)
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(jsonResponse));

    }

    @Test
    void addUserRewardInvalidUserId() throws Exception {

        // GIVEN
        VisitedLocation visitedLocationTest = new VisitedLocation(userId, new Location(56, 25), new Date());
        String jsonBody = objectMapper.writeValueAsString(visitedLocationTest);
        // WHEN

        // THEN
        mockMvc.perform(post(addRewardUrl)
                        .param("userId", "")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MissingServletRequestParameterException));

    }

    @Test
    void addUserRewardInvalidBody() throws Exception {

        // GIVEN
        VisitedLocation visitedLocationTest = new VisitedLocation(null, new Location(56, 25), new Date());
        String invalidJsonBody = objectMapper.writeValueAsString(visitedLocationTest);
        // WHEN

        // THEN
        mockMvc.perform(post(addRewardUrl)
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJsonBody))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));

    }

    @Test
    void addUserRewardValid_ShouldThrowDataAlreadyExistException() throws Exception {

        // GIVEN
        VisitedLocation visitedLocationTest = new VisitedLocation(userId, new Location(56, 25), new Date());
        String validJsonBody = objectMapper.writeValueAsString(visitedLocationTest);
        // WHEN
        doThrow(DataAlreadyExistException.class).when(rewardsServiceMock).addReward(userId, visitedLocationTest);
        // THEN
        mockMvc.perform(post(addRewardUrl)
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJsonBody))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DataAlreadyExistException));
    }

    @Test
    void addUserRewardValid_ShouldThrowResourceNotFoundException() throws Exception {
        // GIVEN
        VisitedLocation visitedLocationTest = new VisitedLocation(userId, new Location(56, 25), new Date());
        String validJsonBody = objectMapper.writeValueAsString(visitedLocationTest);
        // WHEN
        doThrow(ResourceNotFoundException.class).when(rewardsServiceMock).addReward(userId, visitedLocationTest);
        // THEN
        mockMvc.perform(post(addRewardUrl)
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJsonBody))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException));
    }


}