package com.tourguide.rewardservice.service;

import com.tourguide.rewardservice.client.LocationClient;
import com.tourguide.rewardservice.exception.DataAlreadyExistException;
import com.tourguide.rewardservice.exception.IllegalArgumentException;
import com.tourguide.rewardservice.exception.ResourceNotFoundException;
import com.tourguide.rewardservice.model.*;

import com.tourguide.rewardservice.repository.RewardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;

import java.util.*;

@Service
public class RewardsServiceImpl implements RewardsService {


    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

    // proximity in miles
    private final int defaultProximityBuffer = 10;
    private int proximityBuffer = defaultProximityBuffer;
    private final int attractionDefaultProximityRange = 200;
    private int attractionProximityRange = attractionDefaultProximityRange;

    private final RewardCentral rewardsCentral;
    private final RewardRepository rewardRepository;
    private final LocationClient locationClient;
    private final Logger logger = LoggerFactory.getLogger(RewardsServiceImpl.class);

    @Autowired
    public RewardsServiceImpl(RewardCentral rewardsCentral, RewardRepository rewardRepository, LocationClient locationClient) {

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

    public int calculateRewards(UUID userId, UUID attractionId) {

        if (userId == null || attractionId == null) {
            logger.warn(new StringBuilder()
                    .append("error, id can't be null. ")
                    .append("user id: ").append(userId)
                    .append(" / attraction id: ")
                    .append(attractionId).toString());
            throw new IllegalArgumentException("error, id can't be null.");
        }
        int rewardPoints = rewardsCentral.getAttractionRewardPoints(attractionId, userId);
        return rewardPoints;
    }
//        Collection<com.tourguide.rewardservice.model.VisitedLocation> userLocations = locationClient.getAllVisitedLocation(userId);
//        Collection<Attraction> attractions = locationClient.getAllAttractions();
//
//        System.out.println(userLocations.size());
//        int count = 0;
//
//
//        for (VisitedLocation visitedLocation : userLocations) {
//            for (Attraction attraction : attractions) {
//                if (user.getUserRewards().stream().filter(
//                        r -> {
//                            return r.attraction.attractionName.equals(attraction.attractionName);
//                        }).count() == 0) {
//                    if (nearAttraction(visitedLocation, attraction)) {
//                        UserReward newReward = new UserReward(userId, visitedLocation, attraction);
//                        newReward.setRewardPoints(getRewardPoints(attraction, userId));
//                        user.addUserReward(
//                        );
//
//                        count++;
//                        System.out.println("count: " + count);
//                        System.out.println("list size: " + userLocations.size());
//                    }
//                }
//            }
//        }


    /**
     * Add a new UserReward for the given User based on his id.
     *
     * @param userId          the user id
     * @param visitedLocation the location used for the reward
     * @return the UserReward
     */
    @Override
    public UserReward addReward(UUID userId, VisitedLocation visitedLocation) {

        Map<Attraction, Double> nearbyAttractions = locationClient
                .getClosestAttraction(visitedLocation.location().getLatitude(),
                        visitedLocation.location().getLongitude());

        Optional<Attraction> key = nearbyAttractions.keySet().stream().findFirst();

        if (key.isEmpty()) {
            logger.warn("Error, can't get nearby attractions from LocationClient.");
            throw new ResourceNotFoundException("Error, can't get nearby attractions.");
        }

        double distance = nearbyAttractions.get(key.get());
        Attraction attraction = key.get();

        if (!nearAttraction(distance)) {
            logger.warn(new StringBuilder()
                    .append("Error, distance : ")
                    .append(distance)
                    .append(" is superior to proximity buffer : ")
                    .append(proximityBuffer).toString());
            throw new IllegalArgumentException("Error, attraction is not close enough to add a new reward. distance : "
                    + distance);
        }

        Collection<UserReward> userRewards = getUserRewards(userId);

        for (UserReward reward : userRewards) {
            if (reward.getAttraction().attractionId().equals(attraction.attractionId())) {
                logger.warn(new StringBuilder()
                        .append("Error, user : ")
                        .append(userId)
                        .append(" already have a reward for attraction : ")
                        .append(attraction.attractionId()).toString());
                throw new DataAlreadyExistException("Error, there is already a reward for this attractionId.");
            }
        }

        UserReward newReward = new UserReward(userId, visitedLocation, attraction);
        newReward.setRewardPoints(getRewardPoints(attraction.attractionId(), userId));
        rewardRepository.addUserReward(newReward);

        return newReward;
    }


    /**
     * This method check if the attraction is within the proximity range of the location.
     *
     * @param attraction the attraction
     * @param location   the location
     * @return true if they are in range, else return false
     */
    public boolean isWithinAttractionProximity(double distance) {
        return (Math.abs(distance) <= attractionProximityRange);
    }

    /**
     * Check if the visitedLocation is within the range of the attraction.
     * range defined by proximityBuffer
     *
     * @param visitedLocation the visitedLocation
     * @param attraction      the attraction
     * @return true if they are in range, else return false
     */
    private boolean nearAttraction(double distance) {
        return !(Math.abs(distance) > proximityBuffer);
    }

    /**
     * This method get the reward points won by the user by visiting the attraction.
     *
     * @param attraction the attraction
     * @param userId     the user id
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
