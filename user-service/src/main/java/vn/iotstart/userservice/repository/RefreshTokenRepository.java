package vn.iotstart.userservice.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Hidden
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,String> {

    List<RefreshToken> findAllByUser_UserIdAndExpiredIsFalseAndRevokedIsFalse(String userId);
    Optional<RefreshToken> findByUser_UserIdAndExpiredIsFalseAndRevokedIsFalse(String userId);
    Optional<RefreshToken> findByTokenAndExpiredIsFalseAndRevokedIsFalse(String refreshToken);

}

