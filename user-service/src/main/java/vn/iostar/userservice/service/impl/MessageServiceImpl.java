package vn.iostar.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vn.iostar.userservice.constant.KafkaTopicName;
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
    private final KafkaTemplate<String, List<UserResponse>> kafkaTemplate;

    @KafkaListener(topics = KafkaTopicName.FRIEND_TOPIC, groupId = "user-service")
    public void consume(List<String> list_userId) {
        logger.info("Consumed message: " + list_userId);
        List<UserResponse> list = new ArrayList<>();
        for (String userId : list_userId) {
            Optional<User> user = userService.findById(userId);
           if(user.isPresent()){
               UserResponse userResponse = new UserResponse(user.get());
               list.add(userResponse);
           }
        }
        kafkaTemplate.send(KafkaTopicName.USER_TOPIC, list);
        System.out.println("Consumed message: " + list);
    }

}
