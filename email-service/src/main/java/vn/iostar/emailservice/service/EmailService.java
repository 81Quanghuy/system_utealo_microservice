package vn.iostar.emailservice.service;

import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import vn.iostar.emailservice.dto.request.EmailVerificationRequest;
import vn.iostar.emailservice.dto.request.PasswordRequest;
import vn.iostar.emailservice.dto.response.GenericResponse;
import vn.iostar.emailservice.entity.Email;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

public interface EmailService {
    void sendOtp(String email);
    void sendOtpForgotPassword(PasswordRequest email) throws MessagingException, UnsupportedEncodingException;
    ResponseEntity<GenericResponse> verifyOtp(EmailVerificationRequest emailVerificationRequest);
}
