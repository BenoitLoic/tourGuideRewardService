package com.tourguide.rewardservice.model;


import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

public record VisitedLocation(@NotNull UUID userId, @NotNull Location location, Date timeVisited) {

}
