package com.tourguide.rewardservice.model;

import java.util.Date;
import java.util.UUID;

public record VisitedLocation(UUID userId, Location location, Date timeVisited) {

}
