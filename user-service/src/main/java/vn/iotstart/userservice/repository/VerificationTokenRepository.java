package vn.iotstart.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, String> {
    <Optional> VerificationToken findByToken(String token);
}

