package com.tourguide.rewardservice.repository;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.tourguide.rewardservice.model.UserReward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.logging.LogManager;

@Repository
public class RewardRepository {

    private final Logger logger = LoggerFactory.getLogger(RewardRepository.class);
    private final Multimap<UUID, UserReward> internalUserRewardMap = ArrayListMultimap.create();

    public Collection<UserReward> getAllUserRewards(UUID userId) {

        if (!internalUserRewardMap.containsKey(userId)) {
            logger.debug("No reward in repository for userId:" + userId);
            return new ArrayList<>();
        }
        Collection<UserReward> userRewards = internalUserRewardMap.get(userId);
        logger.debug("returning " +
                userRewards.size() +
                " rewards for user : " +
                userId);
        return userRewards;
    }

    public void addUserReward(UserReward userReward) {
        internalUserRewardMap.put(userReward.getVisitedLocation().userId(), userReward);
    }
}
