package vn.iostar.userservice.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.userservice.entity.Token;

import java.util.List;
import java.util.Optional;

@Hidden
@Repository
public interface TokenRepository extends JpaRepository<Token,String> {

    List<Token> findAllByUser_UserIdAndExpiredIsFalseAndRevokedIsFalse(String userId);
    Optional<Token> findByUser_UserIdAndExpiredIsFalseAndRevokedIsFalse(String userId);
    Optional<Token> findByTokenAndExpiredIsFalseAndRevokedIsFalse(String refreshToken);

}

