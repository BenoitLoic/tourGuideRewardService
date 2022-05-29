package com.tourguide.rewardservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rewardCentral.RewardCentral;

/** Configuration class for Reward Service. */
@Configuration
public class RewardServiceConfiguration {

  @Bean
  public RewardCentral rewardCentral() {
    return new RewardCentral();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }
}
