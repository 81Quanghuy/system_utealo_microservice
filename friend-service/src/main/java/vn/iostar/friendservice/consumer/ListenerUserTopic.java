package vn.iostar.friendservice.consumer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import vn.iostar.friendservice.constant.KafkaTopicName;
import vn.iostar.friendservice.dto.response.UserResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListenerUserTopic {
    private final Logger logger = LoggerFactory.getLogger(ListenerUserTopic.class);
    @Setter
    @Getter
    private List<UserResponse> lastReceivedUser;
    //gửi email lỗi lại liên tục 3 lần
    //@RetryableTopic(attempts = "3", dltTopicSuffix = "dlt",backoff = @Backoff(delay = 1000, maxDelay = 10000, multiplier = 2))
    @KafkaListener(topics = KafkaTopicName.USER_TOPIC, groupId = "friend-service")
    public void consume(List<UserResponse> user) {
        logger.info("Consumed message: " + user);
        System.out.println("Consumed message: " + user);
        this.setLastReceivedUser(user); //lưu lại thông tin user
    }
}
