package com.tourguide.rewardservice.service;

import com.tourguide.rewardservice.client.LocationClient;
import com.tourguide.rewardservice.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class RewardsServiceImpl implements RewardsService {


    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

    // proximity in miles
    private final int defaultProximityBuffer = 10;
    private int proximityBuffer = defaultProximityBuffer;
    private final int attractionProximityRange = 200;

    private final RewardCentral rewardsCentral;

    @Autowired
    LocationClient locationClient;

    public RewardsServiceImpl(RewardCentral rewardCentral) {
        this.rewardsCentral = rewardCentral;
    }


    public void setProximityBuffer(int proximityBuffer) {
        this.proximityBuffer = proximityBuffer;
    }

    public void setDefaultProximityBuffer() {
        proximityBuffer = defaultProximityBuffer;
    }


    public void calculateRewards(UUID userId) {
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
    }

    /**
     * This method check if the attraction is within the proximity range of the location.
     *
     * @param attraction the attraction
     * @param location   the location
     * @return true if they are in range, else return false
     */
    public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
        return !(getDistance(attraction.location(), location) > attractionProximityRange);
    }

    /**
     * Check if the visitedLocation is within the range of the attraction.
     * range defined by proximityBuffer
     *
     * @param visitedLocation the visitedLocation
     * @param attraction      the attraction
     * @return true if they are in range, else return false
     */
    private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return !(getDistance(attraction.location(), visitedLocation.location()) > proximityBuffer);
    }

    /**
     * This method get the reward points won by the user by visiting the attraction.
     *
     * @param attraction the attraction
     * @param userId     the user id
     * @return number of points (integer) won by the user
     */
    private int getRewardPoints(Attraction attraction, UUID userId) {
        return rewardsCentral.getAttractionRewardPoints(attraction.attractionId(), userId);
    }

    /**
     * Calculate distance between 2 coordinate.
     *
     * @param loc1 location 1 : latitude, longitude
     * @param loc2 location 2 : latitude, longitude
     * @return distance en Miles
     */
    public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
    }


    /**
     * This method get all the UserRewards owned by a user.
     *
     * @param userId the user id
     * @return list of userRewards
     */
    @Override
    public List<UserReward> getUserRewards(UUID userId) {


        return null;
    }
}
