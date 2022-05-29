package com.tourguide.rewardservice.controller;

import com.tourguide.rewardservice.model.UserReward;
import com.tourguide.rewardservice.model.VisitedLocation;
import com.tourguide.rewardservice.service.RewardsService;
import java.util.Collection;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Rest controller for Reward service Api. */
@RestController
@RequestMapping("/rewards")
public class RewardController {

  @Autowired private RewardsService rewardsService;

  @GetMapping("/get")
  public Collection<UserReward> getUserRewards(@RequestParam UUID userId) {

    return rewardsService.getUserRewards(userId);
  }

  @PostMapping("/add")
  @ResponseStatus(HttpStatus.CREATED)
  public UserReward addUserReward(
      @RequestParam UUID userId, @Valid @RequestBody VisitedLocation visitedLocation) {
    return rewardsService.addReward(userId, visitedLocation).join();
  }

  // ADD userReward

  // Get possible reward
  @GetMapping("/getPoint")
  Integer getReward(@RequestParam UUID attractionId, @RequestParam UUID userId) {
    return rewardsService.getRewardPoints(attractionId, userId).join();
  }
}
