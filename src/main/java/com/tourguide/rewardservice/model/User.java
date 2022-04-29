package com.tourguide.rewardservice.model;

import java.util.UUID;

public record User(UUID userId, String userName, String phoneNumber, String emailAddress) {
}
