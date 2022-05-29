package com.tourguide.rewardservice.model;

import java.util.UUID;

/**
 * Attraction record. used to map feign client response of type Attraction.
 *
 * @param attractionName the attraction name
 * @param city the city
 * @param state the state
 * @param attractionId the attraction id
 * @param location the location
 */
public record Attraction(
        String attractionName,
        String city,
        String state,
        UUID attractionId,
        Location location
){

}
