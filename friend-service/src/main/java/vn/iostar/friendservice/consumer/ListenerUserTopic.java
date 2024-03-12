package vn.iostar.friendservice.consumer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vn.iostar.friendservice.constant.KafkaTopicName;
import vn.iostar.friendservice.dto.response.FriendResponse;
import vn.iostar.friendservice.dto.response.UserResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListenerUserTopic {
    private final Logger logger = LoggerFactory.getLogger(ListenerUserTopic.class);
    private final KafkaTemplate<String, List<FriendResponse>> kafkaTemplate;
    @Setter
    @Getter
    private  List<FriendResponse> lastReceivedUser;

    @KafkaListener(topics = KafkaTopicName.GET_USER_INF_FRIEND_TOPIC, groupId = "friend-service")
    public void consume(List<FriendResponse> user) {
        logger.info("Consumed message: " + user);
        System.out.println("Consumed message: " + user);
        this.setLastReceivedUser(user); //lưu lại thông tin user
    }
}
