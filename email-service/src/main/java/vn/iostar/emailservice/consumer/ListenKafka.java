package vn.iostar.emailservice.consumer;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import vn.iostar.emailservice.constant.KafkaTopicName;
import vn.iostar.emailservice.dto.request.PasswordRequest;
import vn.iostar.emailservice.service.EmailService;

import java.io.UnsupportedEncodingException;

@RequiredArgsConstructor
@Service

public class ListenKafka {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EmailService emailService;

    @KafkaListener(topics = KafkaTopicName.EMAIL_REGISTER_TOPIC, groupId = "email-service")
    public void consume(String email) {
        logger.info("Consumed message: " + email);
        emailService.sendOtp(email);
        System.out.println("Consumed message: " + email);
    }

    @KafkaListener(topics = KafkaTopicName.EMAIL_FORGOT_PASSWORD_TOPIC, groupId = "email-service")
    public void consumeForgotPassword(PasswordRequest email) throws MessagingException, UnsupportedEncodingException {
        logger.info("Consumed message: " + email);
        emailService.sendOtpForgotPassword(email);
        System.out.println("Consumed message: " + email);
    }
}
