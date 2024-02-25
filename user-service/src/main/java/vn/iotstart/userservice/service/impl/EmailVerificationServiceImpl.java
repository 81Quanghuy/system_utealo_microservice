package vn.iotstart.userservice.service.impl;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import vn.iotstart.userservice.entity.Account;
import vn.iotstart.userservice.entity.EmailVerification;
import vn.iotstart.userservice.repository.EmailVerificationRepository;
import vn.iotstart.userservice.service.EmailVerificationService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@EnableScheduling
public class EmailVerificationServiceImpl implements EmailVerificationService {
	private final int OTP_LENGTH = 6;
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private EmailVerificationRepository emailVerificationRepository;
	@Autowired
	private AccountRepository userRepository;
	@Autowired
	TemplateEngine templateEngine;

	@Override
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
			EmailVerification emailVerification = new EmailVerification();
			emailVerification.setEmail(email);
			emailVerification.setOtp(otp);
			emailVerification.setExpirationTime(expirationTime);

			Optional<EmailVerification> existingEmailVerification = findByEmail(email);
            existingEmailVerification.ifPresent(verification -> emailVerificationRepository.delete(verification));

			emailVerificationRepository.save(emailVerification);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean verifyOtp(String email, String otp) {
		Optional<EmailVerification> emailVerification = emailVerificationRepository.findByEmail(email);
		Optional<Account> optionalUser = userRepository.findByEmail(email);
		if (optionalUser.isPresent() && emailVerification.isPresent() && emailVerification.get().getOtp().equals(otp)) {
			Account user = optionalUser.get();
			user.setVerified(true);
			userRepository.save(user);
			return true;
		}
		return false;
	}

	private String generateOtp() {
		StringBuilder otp = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < OTP_LENGTH; i++) {
			otp.append(random.nextInt(10));
		}
		return otp.toString();
	}

	@Override
	public Optional<EmailVerification> findByEmail(String email) {
		return emailVerificationRepository.findByEmail(email);
	}

	@Override
	public void deleteExpiredOtp() {
		LocalDateTime now = LocalDateTime.now();
		List<EmailVerification> expiredOtpList = emailVerificationRepository.findByExpirationTimeBefore(now);
		emailVerificationRepository.deleteAll(expiredOtpList);
	}

	@Scheduled(fixedDelay = 30000) // 5 minutes
	public void cleanupExpiredOtp() {
		deleteExpiredOtp();
	}
}