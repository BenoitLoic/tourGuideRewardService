package com.tourguide.rewardservice.model;

import java.util.UUID;

public record Attraction(
        String attractionName,
        String city,
        String state,
        UUID attractionId,
        Location location
){

}
