package vn.iotstart.userservice.service;

import org.springframework.http.ResponseEntity;
import vn.iotstart.userservice.dto.response.GenericResponse;

public interface RefreshTokenService {
    <S extends RefreshToken> S save(S entity);

    ResponseEntity<GenericResponse> refreshAccessToken(String refreshToken);

    void revokeRefreshToken(String userId);

    ResponseEntity<?> logout(String refreshToken);
}
