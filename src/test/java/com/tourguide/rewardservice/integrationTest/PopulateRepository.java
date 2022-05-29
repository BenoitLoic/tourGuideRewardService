package com.tourguide.rewardservice.integrationTest;

import com.tourguide.rewardservice.model.Attraction;
import com.tourguide.rewardservice.model.Location;
import com.tourguide.rewardservice.model.UserReward;
import com.tourguide.rewardservice.model.VisitedLocation;
import com.tourguide.rewardservice.repository.RewardRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.IntStream;

public class PopulateRepository {

  private final RewardRepository rewardRepository;

  public PopulateRepository(RewardRepository rewardRepository, int internalUserNumber) {
    this.rewardRepository = rewardRepository;
    generateUserRewardRepository(internalUserNumber);
  }

  private void generateUserRewardRepository(int internalUserNumber) {

    List<Attraction> attractions = new ArrayList<>();
    attractions.add(
        new Attraction(
            "Disneyland",
            "Anaheim",
            "CA",
            UUID.randomUUID(),
            new Location(33.817595, -117.922008)));
    attractions.add(
        new Attraction(
            "Jackson Hole",
            "Jackson Hole",
            "WY",
            UUID.randomUUID(),
            new Location(43.582767, -110.821999)));
    attractions.add(
        new Attraction(
            "Mojave National Preserve",
            "Kelso",
            "CA",
            UUID.randomUUID(),
            new Location(35.141689, -115.510399)));
    attractions.add(
        new Attraction(
            "Joshua Tree National Park",
            "Joshua Tree National Park",
            "CA",
            UUID.randomUUID(),
            new Location(33.881866, -115.90065)));
    attractions.add(
        new Attraction(
            "Buffalo National River",
            "St Joe",
            "AR",
            UUID.randomUUID(),
            new Location(35.985512, -92.757652)));
    attractions.add(
        new Attraction(
            "Hot Springs National Park",
            "Hot Springs",
            "AR",
            UUID.randomUUID(),
            new Location(34.52153, -93.042267)));
    attractions.add(
        new Attraction(
            "Kartchner Caverns State Park",
            "Benson",
            "AZ",
            UUID.randomUUID(),
            new Location(31.837551, -110.347382)));
    attractions.add(
        new Attraction(
            "Legend Valley",
            "Thornville",
            "OH",
            UUID.randomUUID(),
            new Location(39.937778, -82.40667)));

    for (int i = 0; i < internalUserNumber; i++) {
      String userNumber = String.format("%06d", i);
      UUID userId = UUID.fromString("0000-00-00-00-" + userNumber);
      IntStream.range(0, 2)
          .forEach(
              j ->
                  rewardRepository.addUserReward(
                      new UserReward(
                          userId,
                          new VisitedLocation(
                              userId,
                              new Location(generateRandomLatitude(), generateRandomLongitude()),
                              getRandomTime()),
                          attractions.get(IntStream.range(0, 8).findAny().getAsInt()))));
    }
  }

  @SuppressFBWarnings("DMI_RANDOM_USED_ONLY_ONCE")
  private double generateRandomLongitude() {
    double leftLimit = -180;
    double rightLimit = 180;
    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
  }

  @SuppressFBWarnings("DMI_RANDOM_USED_ONLY_ONCE")
  private double generateRandomLatitude() {
    double leftLimit = -85.05112878;
    double rightLimit = 85.05112878;
    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
  }

  @SuppressFBWarnings("DMI_RANDOM_USED_ONLY_ONCE")
  private Date getRandomTime() {
    LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
  }
}
