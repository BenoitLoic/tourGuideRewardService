package com.tourguide.rewardservice.service;

import com.tourguide.rewardservice.model.UserReward;
import com.tourguide.rewardservice.model.VisitedLocation;
import java.util.Collection;
import java.util.UUID;

/**
 * Interface for RewardService.
 * Contain methods used by Reward Rest Controller.
 */
public interface RewardsService {

  /**
   * This method get all the UserRewards owned by a user.
   *
   * @param userId the user id
   * @return list of userRewards
   */
  Collection<UserReward> getUserRewards(UUID userId);

  /**
   * Add a new UserReward for the given User based on his id.
   *
   * @param userId the user id
   * @param visitedLocation the location used for the reward
   * @return the UserReward
   */
  UserReward addReward(UUID userId, VisitedLocation visitedLocation);

  /**
   * Get the reward point for the given attraction/user combination.
   * @param attractionId the attraction id
   * @param userId the user id
   * @return the reward point
   */
  Integer getRewardPoint(UUID attractionId, UUID userId);
}
