# tourGuideRewardService
Reward Service for TourGuide APP

### Start application

* copy/clone this project on your computer.
* cd PATH/TO/PROJECT/ROOT where build.gradle is present
* run: ./gradlew bootJar
* run: docker build . -t tourguide/rewardservice
* Access endpoints on localhost:8083/
* test run: ./gradlew test

## Technical:

1. Java : 17
2. Gradle 7+
3. SpringBoot : 2.6.6
4. Docker

## EndPoints

* GET: "/rewards/get"
    * parameter: userId
    * return: collection of UserReward
* POST: "/rewards/add"
    * parameter: visitedLocation
    * return: UserReward added
* GET: "/rewards/getPoint"
    * parameter: attractionId, userId
    * return: integer (points if visited)