package com.tourguide.rewardservice;

import com.tourguide.rewardservice.client.LocationClient;
import com.tourguide.rewardservice.model.Attraction;
import com.tourguide.rewardservice.client.UserClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;

@SpringBootTest
class RewardserviceApplicationTests {

	@Autowired UserClient userClient;
	@Autowired
	LocationClient locationClient;

	@Test
	void contextLoads() {
	}

	@Test
	void test(){



//		List<User> userList = userClient.getAllUsers();
//		System.out.println(userList.size());
//		userList.forEach(i-> System.out.println(i.userName()));

//		Map<UUID, Location> locations = locationClient.getAllLastLocation();
//
//		locations.keySet().forEach(System.out::println);


		Collection<Attraction> attractions = locationClient.getAllAttractions();
		attractions.forEach(System.out::println);

	}

}
