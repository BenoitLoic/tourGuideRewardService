package com.tourguide.rewardservice.service;

import com.tourguide.rewardservice.client.LocationClient;
import com.tourguide.rewardservice.exception.IllegalArgumentException;
import com.tourguide.rewardservice.exception.ResourceNotFoundException;
import com.tourguide.rewardservice.model.Attraction;
import com.tourguide.rewardservice.model.AttractionWithDistanceDto;
import com.tourguide.rewardservice.model.Location;
import com.tourguide.rewardservice.model.UserReward;
import com.tourguide.rewardservice.model.VisitedLocation;
import com.tourguide.rewardservice.repository.RewardRepository;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;

/** Implementation for RewardService. Contain methods used by RewardService Rest controller. */
@Service
public class RewardsServiceImpl implements RewardsService {

  // proximity in miles
  private final int defaultProximityBuffer = Integer.MAX_VALUE;
  private int proximityBuffer = defaultProximityBuffer;
  private final int attractionDefaultProximityRange = 20;
  private int attractionProximityRange = attractionDefaultProximityRange;
  private final RewardCentral rewardsCentral;
  private final RewardRepository rewardRepository;
  private final LocationClient locationClient;
  private final Logger logger = LoggerFactory.getLogger(RewardsServiceImpl.class);
  private final ExecutorService threadPool = Executors.newFixedThreadPool(200);

  /**
   * Constructor for injection. Cop
   *
   * @param rewardsCentral RewardCentral bean
   * @param rewardRepository the repository
   * @param locationClient feign client for location api
   */
  public RewardsServiceImpl(
      RewardCentral rewardsCentral,
      RewardRepository rewardRepository,
      LocationClient locationClient) {
    this.rewardsCentral = rewardsCentral;
    this.rewardRepository = rewardRepository;
    this.locationClient = locationClient;
  }

  public void setProximityBuffer(int proximityBuffer) {
    this.proximityBuffer = proximityBuffer;
  }

  public void setProximityBufferToDefault() {
    proximityBuffer = defaultProximityBuffer;
  }

  public void setAttractionProximityRange(int attractionProximityRange) {
    this.attractionProximityRange = attractionProximityRange;
  }

  /**
   * This method get the reward points won by the user by visiting the attraction.
   *
   * @param attractionId the attraction id
   * @param userId the user id
   * @return number of points (integer) won by the user
   */
  public int calculateRewards(UUID attractionId, UUID userId) {

    if (userId == null || attractionId == null) {
      logger.warn(
          "error, id can't be null. " + "user id: " + userId + " / attraction id: " + attractionId);
      throw new IllegalArgumentException("error, id can't be null.");
    }
    return rewardsCentral.getAttractionRewardPoints(attractionId, userId);
  }

  /**
   * Add a new UserReward for the given User based on his id.
   *
   * @param userId the user id
   * @param visitedLocation the location used for the reward
   * @return the UserReward
   */
  @Override
  public CompletableFuture<UserReward> addReward(UUID userId, VisitedLocation visitedLocation) {

    AttractionWithDistanceDto nearbyAttraction =
        locationClient.getClosestAttraction(
            visitedLocation.location().getLatitude(), visitedLocation.location().getLongitude());

    if (nearbyAttraction == null || nearbyAttraction.getAttractionId() == null) {
      logger.warn("Error, can't get nearby attractions from LocationClient.");
      throw new ResourceNotFoundException("Error, can't get nearby attractions.");
    }

    if (!nearAttraction(nearbyAttraction.getDistance())) {
      logger.warn(
          "Error, distance : "
              + nearbyAttraction.getDistance()
              + " is superior to proximity buffer : "
              + proximityBuffer);
      throw new IllegalArgumentException(
          "Error, attraction is not close enough to add a new reward. distance : "
              + nearbyAttraction.getDistance());
    }

    Attraction attraction =
        new Attraction(
            nearbyAttraction.getAttractionName(),
            nearbyAttraction.getCity(),
            nearbyAttraction.getState(),
            nearbyAttraction.getAttractionId(),
            new Location(
                nearbyAttraction.getLocation().getLongitude(),
                nearbyAttraction.getLocation().getLatitude()));
    UserReward newReward = new UserReward(userId, visitedLocation, attraction);

    rewardRepository.addUserReward(newReward);

    return getRewardPoints(attraction.attractionId(), userId)
        .thenApply(
            p -> {
              newReward.setRewardPoints(p);
              return newReward;
            });
  }

  /**
   * This method check if the attraction is within the proximity range of the location.
   *
   * @param distance the distance between the UserLocation and the attraction
   * @return true if they are in range, else return false
   */
  public boolean isWithinAttractionProximity(double distance) {
    return (Math.abs(distance) <= attractionProximityRange);
  }

  /**
   * Check if the visitedLocation is within the range of the attraction. range defined by
   * proximityBuffer
   *
   * @param distance the distance between the UserLocation and the attraction
   * @return true if they are in range, else return false
   */
  private boolean nearAttraction(double distance) {
    return !(Math.abs(distance) > proximityBuffer);
  }

  /**
   * This method get the reward points won by the user by visiting the attraction.
   *
   * @param attractionId the attraction id
   * @param userId the user id
   * @return number of points (integer) won by the user
   */
  @Override
  public CompletableFuture<Integer> getRewardPoints(UUID attractionId, UUID userId) {
    return CompletableFuture.supplyAsync(
        () -> rewardsCentral.getAttractionRewardPoints(attractionId, userId), threadPool);
  }

  /**
   * This method get all the UserRewards owned by a user.
   *
   * @param userId the user id
   * @return list of userRewards
   */
  @Override
  public Collection<UserReward> getUserRewards(UUID userId) {

    return rewardRepository.getAllUserRewards(userId);
  }
}
