package vn.iostar.userservice.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.userservice.entity.Token;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token,String> {

    // Tìm kiếm token theo userId và token chưa hết hạn
    // Trả về danh sách token
    List<Token> findAllByUser_UserIdAndIsExpiredIsFalseAndIsRevokedIsFalse(String userId);
    // Tìm kiếm token theo refreshToken và token chưa hết hạn
    Optional<Token> findByTokenAndExpiredAtIsFalseAndIsRevokedIsFalse(String refreshToken);

    Optional<Token> findByToken(String token);

    // lấy token đã hết hạn và chưa bị thu hồi
    List<Token> findAllByExpiredAtBefore(Date expiredAt);
}

