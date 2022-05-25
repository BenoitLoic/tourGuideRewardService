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
  private Location location;
  private double distance;
  private UUID attractionId;

  public AttractionWithDistanceDto() {}

  /**
   * Constructor without attraction id and distance.
   *
   * @param attractionName the attraction name
   * @param city the city
   * @param state the state
   * @param location the location
   */
  public AttractionWithDistanceDto(
          String attractionName, String city, String state, Location location) {
    this.attractionName = attractionName;
    this.city = city;
    this.state = state;
    this.location=location;
  }

  /**
   * Constructor without attraction id.
   *
   * @param attractionName the attraction name
   * @param city the city
   * @param state the state
   * @param location the location
   * @param distance the distance
   */
  public AttractionWithDistanceDto(
          String attractionName,
          String city,
          String state,Location location,
          double distance) {
    this.attractionName = attractionName;
    this.city = city;
    this.state = state;
    this.location=location;
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


  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AttractionWithDistanceDto that = (AttractionWithDistanceDto) o;
    return Double.compare(that.distance, distance) == 0 && Objects.equals(attractionName, that.attractionName) && Objects.equals(city, that.city) && Objects.equals(state, that.state) && Objects.equals(location, that.location) && Objects.equals(attractionId, that.attractionId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(attractionName, city, state, location, distance, attractionId);
  }

  @Override
  public String toString() {
    return "AttractionWithDistanceDto{" +
            "attractionName='" + attractionName + '\'' +
            ", city='" + city + '\'' +
            ", state='" + state + '\'' +
            ", location=" + location +
            ", distance=" + distance +
            ", attractionId=" + attractionId +
            '}';
  }
}
