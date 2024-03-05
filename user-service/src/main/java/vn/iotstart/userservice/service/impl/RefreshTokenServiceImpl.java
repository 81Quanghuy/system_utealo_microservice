package vn.iotstart.userservice.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import vn.iotstart.userservice.dto.response.GenericResponse;
import vn.iotstart.userservice.entity.Account;
import vn.iotstart.userservice.entity.User;
import vn.iotstart.userservice.repository.AccountRepository;
import vn.iotstart.userservice.repository.RefreshTokenRepository;
import vn.iotstart.userservice.repository.UserRepository;
import vn.iotstart.userservice.security.JwtTokenProvider;
import vn.iotstart.userservice.security.UserDetail;
import vn.iotstart.userservice.security.UserDetailService;
import vn.iotstart.userservice.service.RefreshTokenService;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    UserDetailService userDetailService;
    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Override
    public <S extends RefreshToken> S save(S entity) {
        return refreshTokenRepository.save(entity);
    }

    @Override
    public ResponseEntity<GenericResponse> refreshAccessToken(String refreshToken) {
        try {
            String userId = jwtTokenProvider.getUserIdFromJwt(refreshToken);
            Optional<Account> optionalUser = accountRepository.findByUserUserId(userId);
            if (optionalUser.isPresent() && optionalUser.get().getIsActive()) {
                // List<RefreshToken> refreshTokens =
                // refreshTokenRepository.findAllByUser_UserIdAndExpiredIsFalseAndRevokedIsFalse(userId);
                Optional<RefreshToken> token = refreshTokenRepository
                        .findByUser_UserIdAndExpiredIsFalseAndRevokedIsFalse(userId);
                if (token.isPresent() && jwtTokenProvider.validateToken(token.get().getToken())) {
                    if (!token.get().getToken().equals(refreshToken)) {
                        return ResponseEntity.status(404)
                                .body(GenericResponse.builder().success(false)
                                        .message("RefreshToken is not present. Please login again!").result("")
                                        .statusCode(HttpStatus.NOT_FOUND.value()).build());
                    }
                    UserDetail userDetail = (UserDetail) userDetailService
                            .loadUserByUserId(jwtTokenProvider.getUserIdFromJwt(refreshToken));
                    String accessToken = jwtTokenProvider.generateAccessToken(userDetail);
                    Map<String, String> resultMap = new HashMap<>();
                    resultMap.put("accessToken", accessToken);
                    resultMap.put("refreshToken", refreshToken);
                    resultMap.put("userId", userDetail.getUserId());
                    return ResponseEntity.status(200).body(GenericResponse.builder().success(true).message("")
                            .result(resultMap).statusCode(HttpStatus.OK.value()).build());
                }
            }
            return ResponseEntity.status(401)
                    .body(GenericResponse.builder().success(false).message("Unauthorized. Please login again!")
                            .result("").statusCode(HttpStatus.UNAUTHORIZED.value()).build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(GenericResponse.builder().success(false).message(e.getMessage())
                    .result("").statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
        }
    }

    @Override
    public void revokeRefreshToken(String userId) {
        try {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent() && optionalUser.get().getIsActive()) {
                List<RefreshToken> refreshTokens = refreshTokenRepository
                        .findAllByUser_UserIdAndExpiredIsFalseAndRevokedIsFalse(userId);

                if (refreshTokens.isEmpty()) {
                    return;
                }
                refreshTokens.forEach(token -> {
                    token.setRevoked(true);
                    token.setExpired(true);
                });
                refreshTokenRepository.saveAll(refreshTokens);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResponseEntity<?> logout(String refreshToken) {
        try {
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(buildErrorResponse("Logout failed!", HttpStatus.UNAUTHORIZED));
            }

            return refreshTokenRepository.findByTokenAndExpiredIsFalseAndRevokedIsFalse(refreshToken).map(token -> {
                token.setRevoked(true);
                token.setExpired(true);
                refreshTokenRepository.save(token);
                SecurityContextHolder.clearContext();
                return ResponseEntity.ok(buildSuccessResponse("Logout successfully!"));
            }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(buildErrorResponse("Logout failed!", HttpStatus.NOT_FOUND)));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    private GenericResponse buildSuccessResponse(String message) {
        return GenericResponse.builder().success(true).message(message).result("").statusCode(HttpStatus.OK.value())
                .build();
    }

    private GenericResponse buildErrorResponse(String message, HttpStatus status) {
        return GenericResponse.builder().success(false).message(message).result("").statusCode(status.value()).build();
    }

}
