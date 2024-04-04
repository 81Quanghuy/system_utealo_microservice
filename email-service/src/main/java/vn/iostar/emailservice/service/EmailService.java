package vn.iostar.emailservice.service;

import jakarta.mail.MessagingException;
import vn.iostar.emailservice.dto.request.PasswordRequest;

import java.io.UnsupportedEncodingException;

public interface EmailService {
    void sendOtp(String email);

    void sendOtpForgotPassword(PasswordRequest email) throws MessagingException, UnsupportedEncodingException;
}
