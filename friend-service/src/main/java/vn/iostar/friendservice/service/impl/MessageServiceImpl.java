package vn.iostar.friendservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import vn.iostar.friendservice.service.FriendService;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl {
    private final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
    private final FriendService friendService;

    //gửi email lỗi lại liên tục 3 lần
    @RetryableTopic(attempts = "3", dltTopicSuffix = "dlt",backoff = @Backoff(delay = 1000, maxDelay = 10000, multiplier = 2))
    @KafkaListener(topics = "email_topic", groupId = "email-service")
    public void consume(String email) {
        logger.info("Consumed message: " + email);
        System.out.println("Consumed message: " + email);
    }

}
