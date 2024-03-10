package vn.iostar.emailservice.service.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import vn.iostar.emailservice.entity.Email;
import vn.iostar.emailservice.repository.EmailRepository;
import vn.iostar.emailservice.service.EmailService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import static vn.iostar.emailservice.constant.AppConstant.OTP_LENGTH;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final EmailRepository emailRepository;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    @Override
    @Async
    public void sendOtp(String email) {
        String otp = generateOtp();
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);

            // Load Thymeleaf template
            Context context = new Context();
            context.setVariable("otpCode", otp);
            context.setVariable("verifyEmail", email);
            String mailContent = templateEngine.process("send-otp", context);

            helper.setText(mailContent, true);
            helper.setSubject("The verification token for UTEALO");
            mailSender.send(message);

            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);
            Email emailVerification = new Email();
            emailVerification.setEmail(email);
            emailVerification.setOtp(otp);
            emailVerification.setExpirationTime(expirationTime);

            Optional<Email> existingEmailVerification = findByEmail(email);
            existingEmailVerification.ifPresent(emailRepository::delete);

            emailRepository.save(emailVerification);
        } catch (Exception e) {
               log.error("Error occurred while sending email verification", e);

            throw new RuntimeException(e);
        }
    }

    private String generateOtp() {
        StringBuilder otp = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    public Optional<Email> findByEmail(String email) {
        return emailRepository.findByEmail(email);
    }

}
