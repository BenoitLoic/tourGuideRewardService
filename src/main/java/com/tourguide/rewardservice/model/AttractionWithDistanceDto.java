package com.tourguide.rewardservice.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Dto for Attraction with additional distance field.
 */
public class AttractionWithDistanceDto {

  private String attractionName;
  private String city;
  private String state;
  private double latitude;
  private double longitude;
  private double distance;
  private UUID attractionId;

  public AttractionWithDistanceDto() {}

  /**
   * Constructor used by feign client.
   *
   * @param attractionName the attraction name
   * @param city the city
   * @param state the state
   * @param latitude the latitude
   * @param longitude the longitude
   * @param distance the distance
   */
  public AttractionWithDistanceDto(
      String attractionName,
      String city,
      String state,
      double latitude,
      double longitude,
      double distance) {
    this.attractionName = attractionName;
    this.city = city;
    this.state = state;
    this.latitude = latitude;
    this.longitude = longitude;
    this.distance = distance;
  }

  public String getAttractionName() {
    return attractionName;
  }

  public void setAttractionName(String attractionName) {
    this.attractionName = attractionName;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public UUID getAttractionId() {
    return attractionId;
  }

  public void setAttractionId(UUID attractionId) {
    this.attractionId = attractionId;
  }

  public double getDistance() {
    return distance;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AttractionWithDistanceDto that = (AttractionWithDistanceDto) o;
    return Double.compare(that.distance, distance) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(distance);
  }

  @Override
  public String toString() {
    return "AttractionWithDistanceDto{"
        + "distance="
        + distance
        + ", attractionName='"
        + attractionName
        + '\''
        + ", city='"
        + city
        + '\''
        + ", state='"
        + state
        + '\''
        + ", attractionId="
        + attractionId
        + ", longitude="
        + longitude
        + ", latitude="
        + latitude
        + '}';
  }
}
