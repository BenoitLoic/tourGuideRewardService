package com.tourguide.rewardservice.model;

import java.util.UUID;

/**
 * User record. used to map feign client response of type User.
 *
 * @param userId the user id
 * @param userName the username
 * @param phoneNumber the phone number
 * @param emailAddress the email address
 */
public record User(UUID userId, String userName, String phoneNumber, String emailAddress) {
}
