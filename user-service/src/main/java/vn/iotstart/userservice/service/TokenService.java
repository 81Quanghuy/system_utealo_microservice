package vn.iotstart.userservice.service;


import org.springframework.http.ResponseEntity;
import vn.iotstart.userservice.dto.request.EmailRequest;
import vn.iotstart.userservice.dto.request.TokenRequest;
import vn.iotstart.userservice.dto.response.GenericResponse;
import vn.iotstart.userservice.entity.Token;

import java.util.List;
import java.util.Optional;

public interface TokenService {
    <S extends Token> S save(S entity);

    List<Token> findAll();

    Optional<Token> findById(String id);

    Optional<Token> findByToken(String token);

    boolean existsById(String id);

    long count();

    void deleteById(String id);

    void delete(Token entity);

    void revokeRefreshToken(String credentialId);

    ResponseEntity<GenericResponse> refreshAccessToken(TokenRequest tokenRequest);

    ResponseEntity<GenericResponse> resetPassword(EmailRequest email);
}
