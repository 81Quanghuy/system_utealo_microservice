package vn.iostar.emailservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import vn.iostar.emailservice.service.EmailService;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl {
    private final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
    private final EmailService emailService;

    @KafkaListener(topics = "email-topic", groupId = "email-service")
    public void consume(String email) {
        logger.info("Consumed message: " + email);
        emailService.sendOtp(email);
        System.out.println("Consumed message: " + email);
    }

}
