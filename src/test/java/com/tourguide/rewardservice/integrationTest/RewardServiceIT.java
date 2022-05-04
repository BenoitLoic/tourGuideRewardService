package com.tourguide.rewardservice.integrationTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourguide.rewardservice.client.LocationClient;
import com.tourguide.rewardservice.exception.DataAlreadyExistException;
import com.tourguide.rewardservice.exception.IllegalArgumentException;
import com.tourguide.rewardservice.model.AttractionWithDistanceDto;
import com.tourguide.rewardservice.model.Location;
import com.tourguide.rewardservice.model.UserReward;
import com.tourguide.rewardservice.model.VisitedLocation;
import com.tourguide.rewardservice.repository.RewardRepository;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RewardServiceIT {

  private final String getUserRewardUrl = "/rewards/get";
  private final String addUserRewardUrl = "/rewards/add";

  @Autowired MockMvc mockMvc;
  @Autowired RewardRepository rewardRepository;
  @Autowired ObjectMapper objectMapper;
  @MockBean LocationClient locationClientMock;

  @Test
  void getUserRewards() throws Exception {

    // When user have reward
    UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000003");
    mockMvc
        .perform(get(getUserRewardUrl).param("userId", userId.toString()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            result -> {
              UserReward[] rewards =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), UserReward[].class);
              assertThat(Arrays.stream(rewards).findAny().get().getUserId()).isEqualTo(userId);
              assertThat(Arrays.stream(rewards).count()).isEqualTo(2);
            });

    // when user have no reward
    UUID unknownUserId = UUID.fromString("10000000-0000-0000-0000-000000000000");
    mockMvc
        .perform(get(getUserRewardUrl).param("userId", unknownUserId.toString()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(result -> assertThat(result.getResponse().getContentAsString()).isEqualTo("[]"));

    // when parameter is not valid
    mockMvc
        .perform(get(getUserRewardUrl).param("userId", "").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertTrue(
                    result.getResolvedException()
                        instanceof MissingServletRequestParameterException));
  }

  @Test
  void addUserReward() throws Exception {

    // mock client response
    AttractionWithDistanceDto clientResponse =
        new AttractionWithDistanceDto("Legend Valley", "Thornville", "OH", 39.937778, -82.40667, 5);
    AttractionWithDistanceDto clientResponseTooFar =
        new AttractionWithDistanceDto("testName", "Thornville", "OH", 39.937778, -82.40667, 15);
    clientResponse.setAttractionId(UUID.randomUUID());
    clientResponseTooFar.setAttractionId(UUID.randomUUID());
    when(locationClientMock.getClosestAttraction(anyDouble(), anyDouble()))
        .thenReturn(clientResponse, clientResponse, clientResponseTooFar);
    UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000003");
    Date date = new Date();
    //    ("Legend Valley", "Thornville", "OH", 39.937778, -82.40667)

    VisitedLocation visitedLocationValid =
        new VisitedLocation(userId, new Location(-82.40667, 39.937778), date);
    String jsonBody = objectMapper.writeValueAsString(visitedLocationValid);
    // when valid and no reward for this location
    mockMvc
        .perform(
            post(addUserRewardUrl)
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            result ->
                assertThat(
                        objectMapper
                            .readValue(result.getResponse().getContentAsString(), UserReward.class)
                            .getAttraction()
                            .attractionName())
                    .isEqualTo("Legend Valley"));
    // when valid but reward already exist
    mockMvc
        .perform(
            post(addUserRewardUrl)
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
        .andExpect(status().isConflict())
        .andExpect(
            result ->
                assertTrue(result.getResolvedException() instanceof DataAlreadyExistException));
    // when parameter not valid
    String invalidBody = jsonBody.replace("00000000-0000-0000-0000-000000000003", "");
    mockMvc
        .perform(
            post(addUserRewardUrl)
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidBody))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertTrue(
                    result.getResolvedException() instanceof MethodArgumentNotValidException));
    // when attraction is too far
    mockMvc
        .perform(
            post(addUserRewardUrl)
                .param("userId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
        .andExpect(status().isBadRequest())
        .andExpect(
            result ->
                assertTrue(
                    result.getResolvedException() instanceof IllegalArgumentException));
  }
}
