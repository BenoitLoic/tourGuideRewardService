package com.tourguide.rewardservice.model;


import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

/**
 * Record for VisitedLocation object.
 * Contain Validation annotation for javax validation.
 *
 * @param userId the user id, can't be null
 * @param location the Location, can't be null
 * @param timeVisited the date
 */
public record VisitedLocation(@NotNull UUID userId, @NotNull Location location, Date timeVisited) {

}
