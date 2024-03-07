package vn.iostar.emailservice.service;

import org.springframework.stereotype.Service;

public interface EmailService {
    void sendOtp(String email);
}
