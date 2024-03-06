package vn.iostar.userservice.service;

import org.springframework.http.ResponseEntity;
import vn.iostar.userservice.dto.response.GenericResponse;
import vn.iostar.userservice.entity.Token;


public interface TokenService {
    <S extends Token> S save(S entity);

    ResponseEntity<GenericResponse> refreshAccessToken(String refreshToken);

    void revokeRefreshToken(String userId);

    ResponseEntity<?> logout(String refreshToken);
}
