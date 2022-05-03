package com.tourguide.rewardservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/** Main Class for Reward Service Api. with @SpringBootApplication and @EnableFeignClients */
@SpringBootApplication
@EnableFeignClients
public class RewardserviceApplication {

  public static void main(String[] args) {
    SpringApplication.run(RewardserviceApplication.class, args);
  }
}
