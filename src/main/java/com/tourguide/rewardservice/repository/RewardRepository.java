package com.tourguide.rewardservice.repository;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.tourguide.rewardservice.model.Attraction;
import com.tourguide.rewardservice.model.Location;
import com.tourguide.rewardservice.model.UserReward;
import com.tourguide.rewardservice.model.VisitedLocation;
import rewardCentral.RewardCentral;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/** Repository for Reward Service. Contains method to read/save UserReward */
@Repository
public class RewardRepository {

  private final int internalUserNumber = 10;
  private final Logger logger = LoggerFactory.getLogger(RewardRepository.class);
  private final Multimap<UUID, UserReward> internalUserRewardMap = ArrayListMultimap.create();

  @Autowired RewardCentral rewardCentral;

  public RewardRepository(RewardCentral rewardCentral) {
    this.rewardCentral = rewardCentral;
    generateUserRewardRepository(internalUserNumber);
  }

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

  private void generateUserRewardRepository(int internalUserNumber) {

    List<Attraction> attractions = new ArrayList();
    attractions.add(new Attraction("Disneyland", "Anaheim", "CA", UUID.randomUUID(),new Location(33.817595, -117.922008)));
    attractions.add(new Attraction("Jackson Hole", "Jackson Hole", "WY", UUID.randomUUID(),new Location(43.582767, -110.821999)));
    attractions.add(new Attraction("Mojave National Preserve", "Kelso", "CA", UUID.randomUUID(),new Location(35.141689, -115.510399)));
    attractions.add(new Attraction("Joshua Tree National Park", "Joshua Tree National Park", "CA",UUID.randomUUID(),new Location( 33.881866, -115.90065)));
    attractions.add(new Attraction("Buffalo National River", "St Joe", "AR", UUID.randomUUID(),new Location(35.985512, -92.757652)));
    attractions.add(new Attraction("Hot Springs National Park", "Hot Springs", "AR", UUID.randomUUID(),new Location(34.52153, -93.042267)));
    attractions.add(new Attraction("Kartchner Caverns State Park", "Benson", "AZ",UUID.randomUUID(),new Location( 31.837551, -110.347382)));
    attractions.add(new Attraction("Legend Valley", "Thornville", "OH", UUID.randomUUID(),new Location(39.937778, -82.40667)));

    for (int i = 0; i < internalUserNumber; i++) {
      String userNumber = String.format("%06d", i);
      UUID userId = UUID.fromString("0000-00-00-00-" + userNumber);
      IntStream.range(0, 2)
          .forEach(
              j ->
                  internalUserRewardMap.put(
                      userId,
                      new UserReward(
                          userId,
                          new VisitedLocation(
                              userId,
                              new Location(generateRandomLatitude(), generateRandomLongitude()),
                              getRandomTime()),
                          attractions.get(IntStream.range(0,8).findAny().getAsInt()))));
    }
  }

  private double generateRandomLongitude() {
    double leftLimit = -180;
    double rightLimit = 180;
    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
  }

  private double generateRandomLatitude() {
    double leftLimit = -85.05112878;
    double rightLimit = 85.05112878;
    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
  }

  private Date getRandomTime() {
    LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
  }
}
