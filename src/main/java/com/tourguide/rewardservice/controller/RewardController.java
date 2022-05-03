package com.tourguide.rewardservice.controller;

import com.tourguide.rewardservice.model.UserReward;
import com.tourguide.rewardservice.model.VisitedLocation;
import com.tourguide.rewardservice.service.RewardsService;
import javax.validation.Valid;
import java.util.Collection;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rewards")
public class RewardController {

  @Autowired private RewardsService rewardsService;

  @GetMapping("/")
  public String index() {
    return "Welcome to reward microservice !";
  }

  @GetMapping("/get")
  public Collection<UserReward> getUserRewards(@RequestParam UUID userId) {

    return rewardsService.getUserRewards(userId);
  }

  @PostMapping("/add")
  @ResponseStatus(HttpStatus.CREATED)
  public UserReward addUserReward(
      @RequestParam UUID userId, @Valid @RequestBody VisitedLocation visitedLocation) {
    return rewardsService.addReward(userId, visitedLocation);
  }

  // ADD userReward

  // Get possible reward

}
