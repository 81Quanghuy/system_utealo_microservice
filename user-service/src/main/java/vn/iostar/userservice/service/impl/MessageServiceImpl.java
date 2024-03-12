package vn.iostar.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vn.iostar.userservice.constant.KafkaTopicName;
import vn.iostar.userservice.dto.response.UserOfPostResponse;
import vn.iostar.userservice.dto.response.UserResponse;
import vn.iostar.userservice.entity.User;
import vn.iostar.userservice.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl {
    private final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
    private final UserService userService;
    private final KafkaTemplate<String, String> userIdKafkaTemplate;

    @KafkaListener(topics = KafkaTopicName.POST_TOPIC_GET_USER, groupId = "user-service")
    public void receivePostInformation(String userId) {
        logger.info("Consumed message: " + userId);
        Optional<User> user = userService.findById(userId);
        if (user.isPresent()) {
            // Xử lý chỉ khi user tồn tại
            UserOfPostResponse userOfPostResponse = new UserOfPostResponse(user.get());
            userIdKafkaTemplate.send(KafkaTopicName.USER_TOPIC, user.get().getUserName());
            System.out.println("Consumed message: " + user.get().getUserName());
        } else {
            logger.warn("User with ID " + userId + " not found.");
        }

    }
}
