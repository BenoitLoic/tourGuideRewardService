package com.tourguide.rewardservice.model;

import gpsUtil.location.Attraction;


import java.util.Objects;
import java.util.UUID;

public class UserReward {

    private final UUID userId;
private final VisitedLocation visitedLocation;
private final Attraction attraction;
private int rewardPoints;

    public UserReward(UUID userId, VisitedLocation visitedLocation, Attraction attraction) {
        this.userId = userId;
        this.visitedLocation = visitedLocation;
        this.attraction = attraction;
    }

    public VisitedLocation getVisitedLocation() {
        return visitedLocation;
    }

    public Attraction getAttraction() {
        return attraction;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserReward that = (UserReward) o;
        return rewardPoints == that.rewardPoints && visitedLocation.equals(that.visitedLocation) && attraction.equals(that.attraction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(visitedLocation, attraction, rewardPoints);
    }

    @Override
    public String toString() {
        return "UserReward{" +
                "visitedLocation=" + visitedLocation +
                ", attraction=" + attraction +
                ", rewardPoints=" + rewardPoints +
                '}';
    }
}
