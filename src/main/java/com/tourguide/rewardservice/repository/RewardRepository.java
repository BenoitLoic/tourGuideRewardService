package com.tourguide.rewardservice.repository;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.tourguide.rewardservice.model.UserReward;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * Repository for Reward Service.
 * Contains method to read/save UserReward
 */
@Repository
public class RewardRepository {

  private final Logger logger = LoggerFactory.getLogger(RewardRepository.class);
  private final Multimap<UUID, UserReward> internalUserRewardMap = ArrayListMultimap.create();

  /**
   * Get every rewards saved for the user identified by its id.
   *
   * @param userId the user id
   * @return a collection with all user's rewards, if there are none -> return an empty list
   */
  public Collection<UserReward> getAllUserRewards(UUID userId) {

    if (!internalUserRewardMap.containsKey(userId)) {
      logger.debug("No reward in repository for userId:" + userId);
      return new ArrayList<>();
    }
    Collection<UserReward> userRewards = internalUserRewardMap.get(userId);
    logger.debug("returning " + userRewards.size() + " rewards for user : " + userId);
    return userRewards;
  }

  public void addUserReward(UserReward userReward) {
    internalUserRewardMap.put(userReward.getVisitedLocation().userId(), userReward);
  }
}
