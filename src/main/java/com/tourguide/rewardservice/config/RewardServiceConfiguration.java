package com.tourguide.rewardservice.config;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import rewardCentral.RewardCentral;

@Configuration
public class RewardServiceConfiguration {

    @Bean
    public RewardCentral rewardCentral() {
        return new RewardCentral();
    }


    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

}
