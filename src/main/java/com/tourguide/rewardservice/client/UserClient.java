package com.tourguide.rewardservice.client;

import com.tourguide.rewardservice.model.User;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "userservice", url = "http://localhost:8100/")
public interface UserClient {

  @RequestMapping(method = RequestMethod.GET, value = "/getAllUsers")
  List<User> getAllUsers();

  @RequestMapping(method = RequestMethod.GET, value = "/getUserByUsername")
  User getUserByUsername(@RequestParam String username);
}
