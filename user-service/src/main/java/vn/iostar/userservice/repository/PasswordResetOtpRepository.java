package vn.iostar.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.iostar.userservice.entity.PasswordResetOtp;
import vn.iostar.userservice.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, String> {

    Optional<PasswordResetOtp> findByOtp(String otp);

    Optional<PasswordResetOtp> findByUser(User user);

    Stream<PasswordResetOtp> findAllByExpiryDateLessThan(Date now);

    void deleteByExpiryDateLessThan(Date now);

    @Modifying
    @Query("delete from PasswordResetOtp t where t.expiryDate <= ?1")
    void deleteAllExpiredSince(Date now);

    @Modifying
    @Transactional
    @Query("delete from PasswordResetOtp t where t.user in ?1")
    void deleteAllByUserIn(@Param("users") List<User> users);
}
