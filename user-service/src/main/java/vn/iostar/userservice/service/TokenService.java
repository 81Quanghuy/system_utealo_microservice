package vn.iostar.userservice.service;

import org.springframework.http.ResponseEntity;
import vn.iostar.userservice.dto.response.GenericResponse;
import vn.iostar.userservice.entity.Token;


public interface TokenService {

    // Lưu token vào database
    <S extends Token> S save(S entity);

    // Kiểm tra token có hợp lệ không
    ResponseEntity<GenericResponse> refreshAccessToken(String refreshToken);

    // Xóa token khỏi database
    void revokeRefreshToken(String userId);

    // Đăng xuất
    ResponseEntity<?> logout(String refreshToken);
}
