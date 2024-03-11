package vn.iostar.postservice.service.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import vn.iostar.postservice.constant.KafkaTopicName;
import vn.iostar.postservice.constant.KafkaTopicName;
import vn.iostar.postservice.dto.response.PostsResponse;
import vn.iostar.postservice.dto.response.UserOfPostResponse;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl {
    private final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
    @Setter
    @Getter
    private UserOfPostResponse lastReceivedUser;
    //gửi email lỗi lại liên tục 3 lần
    //@RetryableTopic(attempts = "3", dltTopicSuffix = "dlt",backoff = @Backoff(delay = 1000, maxDelay = 10000, multiplier = 2))
    @KafkaListener(topics = KafkaTopicName.USER_TOPIC, groupId = "post-service")
    public void receiveUserInformation(UserOfPostResponse user) {
        logger.info("Consumed message: " + user);
        System.out.println("Consumed message: " + user);
        this.setLastReceivedUser(user); //lưu lại thông tin user
    }
}
