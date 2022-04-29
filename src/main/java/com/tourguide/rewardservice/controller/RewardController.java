package com.tourguide.rewardservice.controller;

import com.tourguide.rewardservice.config.RewardServiceConfiguration;
import com.tourguide.rewardservice.model.UserReward;
import com.tourguide.rewardservice.service.RewardsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rewards")
public class RewardController {

    private RewardsService rewardsService;

    @GetMapping("/")
    public String index(){
        return "Welcome to reward microservice !";
    }


    @GetMapping
    public List<UserReward> getUserRewards(UUID userId){

        return rewardsService.getUserRewards(userId);

    }

    // ADD userReward

    // Get possible reward

}
