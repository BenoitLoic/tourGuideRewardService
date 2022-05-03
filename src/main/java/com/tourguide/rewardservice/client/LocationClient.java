package com.tourguide.rewardservice.client;

import com.tourguide.rewardservice.model.Attraction;
import com.tourguide.rewardservice.model.AttractionWithDistanceDto;
import com.tourguide.rewardservice.model.Location;
import com.tourguide.rewardservice.model.VisitedLocation;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client interface for LocationService Api.
 * Contains method to request data from LocationService REST Api.
 */
@FeignClient(value = "locationService", url = "http://localhost:8110/")
public interface LocationClient {

  @RequestMapping(method = RequestMethod.GET, value = "/getStats")
  Map<UUID, Location> getAllLastLocation();

  @RequestMapping(method = RequestMethod.GET, value = "/location/getAll")
  Collection<VisitedLocation> getAllVisitedLocation(@RequestParam UUID userId);

  @RequestMapping(method = RequestMethod.GET, value = "/attraction/getNearby")
  Collection<Attraction> getNearbyAttractions(
      @RequestParam Double latitude, @RequestParam Double longitude);

  @RequestMapping(method = RequestMethod.GET, value = "/attraction/getAll")
  Collection<Attraction> getAllAttractions();

  @RequestMapping(method = RequestMethod.GET, value = "/attraction/getClosest")
  AttractionWithDistanceDto getClosestAttraction(
      @RequestParam Double latitude, @RequestParam Double longitude);
}
