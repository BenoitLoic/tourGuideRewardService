package com.tourguide.rewardservice.service;

import com.tourguide.rewardservice.client.LocationClient;
import com.tourguide.rewardservice.exception.DataAlreadyExistException;
import com.tourguide.rewardservice.exception.IllegalArgumentException;
import com.tourguide.rewardservice.exception.ResourceNotFoundException;
import com.tourguide.rewardservice.model.Attraction;
import com.tourguide.rewardservice.model.AttractionWithDistanceDto;
import com.tourguide.rewardservice.model.Location;
import com.tourguide.rewardservice.model.UserReward;
import com.tourguide.rewardservice.model.VisitedLocation;
import com.tourguide.rewardservice.repository.RewardRepository;
import org.junit.jupiter.api.Disabled;
import rewardCentral.RewardCentral;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RewardsServiceImplTest {

  private final UUID userId = UUID.randomUUID();
  private final UUID attractionId1 = UUID.randomUUID();
  private final UUID attractionId2 = UUID.randomUUID();

  @Mock RewardsServiceImpl rewardsServiceMock;
  @Mock LocationClient locationClientMock;

  @Mock RewardCentral rewardCentralMock;
  @Mock RewardRepository rewardRepositoryMock;

  @InjectMocks RewardsServiceImpl rewardsService;

  @BeforeEach
  void setUp() {}

  @Test
  void calculateRewards() {
    // -> calcule la récompense pour l'attraction et l'utilisateur donné.
    // fin de chaine
    // param(userId, attractionId)
    // return userReward

    // appel RewardCentral 1 fois avec les bon params
    // renvoi un nombre entre 0 et 1000

    // GIVEN
    int expectedPoints = 100;
    // WHEN
    when(rewardCentralMock.getAttractionRewardPoints(any(), any())).thenReturn(expectedPoints);
    int result = rewardsService.calculateRewards(attractionId1, userId);
    // THEN
    assertThat(result).isEqualTo(expectedPoints);
    verify(rewardCentralMock, times(1)).getAttractionRewardPoints(attractionId1, userId);
  }

  @Test
  void calculateRewards_ShouldThrowIllegalArgumentException() {
    // -> calcule la récompense pour l'attraction et l'utilisateur donné.
    // fin de chaine
    // param(userId, attractionId)
    // return userReward

    // appel RewardCentral 1 fois avec les bon params
    // renvoi un nombre entre 0 et 1000

    // GIVEN

    // WHEN

    // THEN
    assertThrows(
        IllegalArgumentException.class, () -> rewardsService.calculateRewards(null, userId));
    assertThrows(
        IllegalArgumentException.class, () -> rewardsService.calculateRewards(attractionId1, null));
  }

  @Test
  void rewardCentralShouldReturnAnIntegerBetween0And1000() {
    RewardCentral rewardCentral = new RewardCentral();
    int result = rewardCentral.getAttractionRewardPoints(userId, attractionId1);
    assertThat(result).isGreaterThanOrEqualTo(0);
    assertThat(result).isLessThanOrEqualTo(1000);
  }

  @Test
  void addReward() throws ExecutionException, InterruptedException {
    // -> ajoute une récompense
    // récupère les attractions les plus proches.
    // vérifie la proximité de l'utilisateur avec l'attraction la plus proche de sa position.
    // si trop loin -> renvoi une exception
    // sinon : appel calculateRewards
    // appel le repo et sauvegarde la récompense.
    // retourne la userReward
    // param (userId, visitedLocation)
    // return UserReward

    // GIVEN

    Attraction attraction2 =
        new Attraction(
            "attractionNameTest2", "cityTest", "stateTest", attractionId2, new Location(50, 20));
    double distanceOk = 5.0;
    AttractionWithDistanceDto clientResponse =
        new AttractionWithDistanceDto(
            attraction2.attractionName(),
            attraction2.city(),
            attraction2.state(),new Location(
            attraction2.location().getLongitude(),
            attraction2.location().getLatitude()),
            distanceOk);
    clientResponse.setAttractionId(attractionId2);
    Date dateTest = new Date();
    VisitedLocation visitedLocation = new VisitedLocation(userId, new Location(50, 20), dateTest);
    UserReward expectedUserReward = new UserReward(userId, visitedLocation, attraction2);
    expectedUserReward.setRewardPoints(100);
    // WHEN
    doNothing().when(rewardRepositoryMock).addUserReward(any());
    when(rewardCentralMock.getAttractionRewardPoints(attractionId2, userId)).thenReturn(100);
    when(locationClientMock.getClosestAttraction(anyDouble(), anyDouble()))
        .thenReturn(clientResponse);

 rewardsService.addReward(userId, visitedLocation).get();
    // THEN
    verify(rewardRepositoryMock, times(1)).addUserReward(expectedUserReward);
  }

  @Test
  void addReward_ShouldThrowResourceNotFoundException() {
    // GIVEN

    Date dateTest = new Date();
    VisitedLocation visitedLocation = new VisitedLocation(userId, new Location(50, 20), dateTest);

    // WHEN
    when(locationClientMock.getClosestAttraction(anyDouble(), anyDouble()))
        .thenReturn(new AttractionWithDistanceDto());

    // THEN
    assertThrows(
        ResourceNotFoundException.class, () -> rewardsService.addReward(userId, visitedLocation));
  }

  @Test
  void isWithinAttractionProximity_True() {

    // GIVEN
    double isWithinProximityRange = 2d;
    rewardsService.setAttractionProximityRange(10);
    // WHEN
    boolean result = rewardsService.isWithinAttractionProximity(isWithinProximityRange);
    // THEN
    assertThat(result).isTrue();
  }

  @Test
  void isNotWithinAttractionProximity_ShouldReturnFalse() {

    // GIVEN
    double isNotWithinProximityRange = 11;
    rewardsService.setAttractionProximityRange(10);
    // WHEN
    boolean result = rewardsService.isWithinAttractionProximity(isNotWithinProximityRange);
    // THEN
    assertThat(result).isFalse();
  }

  @Test
  void isWithinAttractionProximity_WithNegativeParam_ReturnTrue() {

    // GIVEN
    double isWithinProximityRange = -10d;
    rewardsService.setAttractionProximityRange(10);
    // WHEN
    boolean result = rewardsService.isWithinAttractionProximity(isWithinProximityRange);
    // THEN
    assertThat(result).isTrue();
  }

  @Test
  void getUserRewards() {

    // GIVEN

    UserReward userReward1 =
        new UserReward(
            userId,
            new VisitedLocation(userId, new Location(56, 25), new Date()),
            new Attraction(
                "attractionNameTest1",
                "cityTest",
                "stateTest",
                attractionId1,
                new Location(50, 20)));

    UserReward userReward2 =
        new UserReward(
            userId,
            new VisitedLocation(userId, new Location(50, 28), new Date()),
            new Attraction(
                "attractionNameTest2",
                "cityTest",
                "stateTest",
                attractionId2,
                new Location(50, 20)));
    Collection<UserReward> userRewardsMocked = Arrays.asList(userReward1, userReward2);

    // WHEN
    when(rewardRepositoryMock.getAllUserRewards(userId)).thenReturn(userRewardsMocked);
    Collection<UserReward> result = rewardsService.getUserRewards(userId);
    // THEN

    assertThat(result.size()).isEqualTo(userRewardsMocked.size());
    verify(rewardRepositoryMock, times(1)).getAllUserRewards(userId);
  }

  @Test
  void getUserRewards_ShouldReturnEmptyList() {

    // GIVEN

    // WHEN
    when(rewardRepositoryMock.getAllUserRewards(userId)).thenReturn(new ArrayList<>());
    Collection<UserReward> result = rewardsService.getUserRewards(userId);
    // THEN

    assertThat(result).isEqualTo(new ArrayList<>());
    verify(rewardRepositoryMock, times(1)).getAllUserRewards(userId);
  }
}
