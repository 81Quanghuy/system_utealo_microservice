package vn.iostar.postservice.service.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Getter
    @Setter
    private String lastReceivedUser;

    @KafkaListener(topics = KafkaTopicName.USER_TOPIC, groupId = "post-service")
    public void getUserName(String userName) {
        logger.info("Consumed message: " + userName);
        System.out.println("Consumed message of user: " + userName);
        lastReceivedUser = userName;
        System.out.println("Last received user: " + lastReceivedUser);
    }


}
