package vn.iostar.emailservice.consumer;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import vn.iostar.constant.KafkaTopicName;
import vn.iostar.emailservice.service.EmailService;
import vn.iostar.model.PasswordReset;
import vn.iostar.model.VerifyParent;

import java.io.UnsupportedEncodingException;

@RequiredArgsConstructor
@Service
public class ListenKafka {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EmailService emailService;

    @KafkaListener(topics = KafkaTopicName.EMAIL_REGISTER_TOPIC, groupId = "email-service")
    public void consume(String email) {
        logger.info("Consumed message:",email);
        emailService.sendOtp(email);
        System.out.println(email);
    }

    @KafkaListener(topics = KafkaTopicName.EMAIL_FORGOT_PASSWORD_TOPIC, groupId = "email-service")
    public void consumeForgotPassword(PasswordReset email) throws MessagingException, UnsupportedEncodingException {
        emailService.sendOtpForgotPassword(email);
        System.out.println(email);
    }

    @KafkaListener(topics = KafkaTopicName.EMAIL_VERIFY_TOPIC, groupId = "email-service")
    public void consumeVerify(VerifyParent email) throws MessagingException, UnsupportedEncodingException {
        logger.info("Consumed message:",email);
        emailService.sendVerify(email);
        System.out.println(email);
    }
}
