package com.tourguide.rewardservice.service;

import com.tourguide.rewardservice.model.UserReward;

import java.util.List;
import java.util.UUID;

public interface RewardsService {

    /**
     * This method get all the UserRewards owned by a user.
     * @param userId the user id
     * @return list of userRewards
     */
    List<UserReward> getUserRewards(UUID userId);

}
