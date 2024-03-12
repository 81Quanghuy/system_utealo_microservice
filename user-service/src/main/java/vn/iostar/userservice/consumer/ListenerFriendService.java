package vn.iostar.userservice.consumer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vn.iostar.userservice.constant.KafkaTopicName;
import vn.iostar.userservice.dto.response.FriendResponse;
import vn.iostar.userservice.entity.User;
import vn.iostar.userservice.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ListenerFriendService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserService userService;
    private final KafkaTemplate<String, List<FriendResponse>> kafkaTemplate;

    @Setter
    @Getter
    private List<FriendResponse> lastReceivedUser;

    @KafkaListener(topics = KafkaTopicName.GET_LIST_FRIEND_BY_USERID_TOPIC, groupId = "user-service")
    public void consume(List<String> list_userId) {
        logger.info("Consumed message: " + list_userId);
        List<FriendResponse> friendResponses = new ArrayList<>();
        for (String userId : list_userId) {
            Optional<User> user = userService.findById(userId);
            if(user.isPresent()){
                FriendResponse friendResponse = new FriendResponse(user.get());
                friendResponses.add(friendResponse);
            }
        }
        // Check if all users have been processed
        if (friendResponses.size() == list_userId.size()) {
            kafkaTemplate.send(KafkaTopicName.GET_USER_INF_FRIEND_TOPIC, friendResponses);
            logger.info("Sent friend information to Kafka: " + friendResponses);
        } else {
            logger.warn("Missing user information for some IDs. Not sending to Kafka.");
        }
    }
}
