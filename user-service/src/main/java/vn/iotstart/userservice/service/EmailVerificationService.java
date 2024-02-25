package vn.iotstart.userservice.service;



import vn.iotstart.userservice.entity.EmailVerification;

import java.util.Optional;

public interface EmailVerificationService {
    void sendOtp(String email);
    Optional<EmailVerification> findByEmail(String email);
    void deleteExpiredOtp();
    boolean verifyOtp(String email, String otp);
}
