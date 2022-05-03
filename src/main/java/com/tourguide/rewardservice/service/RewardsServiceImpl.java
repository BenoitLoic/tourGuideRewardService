package com.tourguide.rewardservice.service;

import com.tourguide.rewardservice.client.LocationClient;
import com.tourguide.rewardservice.exception.DataAlreadyExistException;
import com.tourguide.rewardservice.exception.IllegalArgumentException;
import com.tourguide.rewardservice.exception.ResourceNotFoundException;
import com.tourguide.rewardservice.model.Attraction;
import com.tourguide.rewardservice.model.AttractionWithDistanceDto;
import com.tourguide.rewardservice.model.Location;
import com.tourguide.rewardservice.model.UserReward;
import com.tourguide.rewardservice.model.VisitedLocation;
import com.tourguide.rewardservice.repository.RewardRepository;
import rewardCentral.RewardCentral;
import java.util.Collection;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Implementation for RewardService. Contain methods used by RewardService Rest controller. */
@Service
public class RewardsServiceImpl implements RewardsService {

  // proximity in miles
  private final int defaultProximityBuffer = 10;
  private int proximityBuffer = defaultProximityBuffer;
  private final int attractionDefaultProximityRange = 200;
  private int attractionProximityRange = attractionDefaultProximityRange;

  private final RewardCentral rewardsCentral;
  private final RewardRepository rewardRepository;
  private final LocationClient locationClient;
  private final Logger logger = LoggerFactory.getLogger(RewardsServiceImpl.class);

  /**
   * Constructor for injection.
   *
   * @param rewardsCentral RewardCentral bean
   * @param rewardRepository the repository
   * @param locationClient feign client for location api
   */
  @Autowired
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
          new StringBuilder()
              .append("error, id can't be null. ")
              .append("user id: ")
              .append(userId)
              .append(" / attraction id: ")
              .append(attractionId)
              .toString());
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
  public UserReward addReward(UUID userId, VisitedLocation visitedLocation) {

    AttractionWithDistanceDto nearbyAttractions =
        locationClient.getClosestAttraction(
            visitedLocation.location().getLatitude(), visitedLocation.location().getLongitude());

    if (nearbyAttractions == null || nearbyAttractions.getAttractionId() == null) {
      logger.warn("Error, can't get nearby attractions from LocationClient.");
      throw new ResourceNotFoundException("Error, can't get nearby attractions.");
    }

    if (!nearAttraction(nearbyAttractions.getDistance())) {
      logger.warn(
          new StringBuilder()
              .append("Error, distance : ")
              .append(nearbyAttractions.getDistance())
              .append(" is superior to proximity buffer : ")
              .append(proximityBuffer)
              .toString());
      throw new IllegalArgumentException(
          "Error, attraction is not close enough to add a new reward. distance : "
              + nearbyAttractions.getDistance());
    }

    Collection<UserReward> userRewards = getUserRewards(userId);

    for (UserReward reward : userRewards) {
      if (reward.getAttraction().attractionId().equals(nearbyAttractions.getAttractionId())
          || reward
              .getAttraction()
              .attractionName()
              .equals(nearbyAttractions.getAttractionName())) {
        logger.warn(
            new StringBuilder()
                .append("Error, user : ")
                .append(userId)
                .append(" already have a reward for attraction : ")
                .append(nearbyAttractions.getAttractionName())
                .append(" with id: ")
                .append(nearbyAttractions.getAttractionId())
                .toString());
        throw new DataAlreadyExistException(
            "Error, there is already a reward for this attractionId.");
      }
    }

    Attraction attraction =
        new Attraction(
            nearbyAttractions.getAttractionName(),
            nearbyAttractions.getCity(),
            nearbyAttractions.getState(),
            nearbyAttractions.getAttractionId(),
            new Location(nearbyAttractions.getLongitude(), nearbyAttractions.getLatitude()));
    UserReward newReward = new UserReward(userId, visitedLocation, attraction);
    newReward.setRewardPoints(getRewardPoints(attraction.attractionId(), userId));
    rewardRepository.addUserReward(newReward);

    return newReward;
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
  private int getRewardPoints(UUID attractionId, UUID userId) {
    return rewardsCentral.getAttractionRewardPoints(attractionId, userId);
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
