package vn.iotstart.userservice.service.impl;

import com.cloudinary.provisioning.Account;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;
    private final CredentialRepository credentialRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final UserRepository userRepository;


    @Override
    public <S extends Token> S save(S entity) {
        return tokenRepository.save(entity);
    }

    @Override
    public List<Token> findAll() {
        return tokenRepository.findAll();
    }

    @Override
    public Optional<Token> findById(String id) {
        return tokenRepository.findById(id);
    }

    @Override
    public Optional<Token> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public boolean existsById(String id) {
        return tokenRepository.existsById(id);
    }

    @Override
    public long count() {
        return tokenRepository.count();
    }

    @Override
    public void deleteById(String id) {
        tokenRepository.deleteById(id);
    }

    @Override
    public void delete(Token entity) {
        tokenRepository.delete(entity);
    }

    @Override
    public void revokeRefreshToken(String credentialId) {
        log.info("TokenServiceImpl, void, revokeRefreshToken");
        Optional<Credential> optionalCredential = credentialRepository.findById(credentialId);
        if (optionalCredential.isPresent() && optionalCredential.get().getIsEnabled()) {
            List<Token> refreshTokens = tokenRepository.findActiveRefreshTokens(credentialId, TokenType.REFRESH_ACCESS_TOKEN);
            if(!refreshTokens.isEmpty()){
                refreshTokens.forEach(token -> {
                    token.setIsRevoked(true);
                    token.setIsExpired(true);
                });
            }
            tokenRepository.saveAll(refreshTokens);
            return;
        }
        throw new BadRequestException("Tài khoản không tồn tại hoặc chưa được xác thực!");
    }

    @Override
    public ResponseEntity<GenericResponse> refreshAccessToken(TokenRequest tokenRequest) {
        String refreshToken = tokenRequest.getRefreshToken();
        Optional<Account> optionalCredential = getValidCredentialFromRefreshToken(refreshToken);
        if (optionalCredential.isPresent()) {
            String newAccessToken = jwtService.generateAccessToken(optionalCredential.get());
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("accessToken", newAccessToken);
            resultMap.put("refreshToken", refreshToken);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(GenericResponse.builder()
                            .success(true)
                            .message("Làm mới token thành công!")
                            .result(resultMap)
                            .statusCode(HttpStatus.OK.value())
                            .build());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(GenericResponse.builder()
                        .success(false)
                        .message("Không xác thực! Vui lòng đăng nhập lại!")
                        .result(null)
                        .statusCode(HttpStatus.FORBIDDEN.value())
                        .build());
    }

    private Optional<Credential> getValidCredentialFromRefreshToken(String refreshToken) {
        try {
            if (jwtService.validateToken(refreshToken)) {
                String userId = jwtService.extractUserId(refreshToken);
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng!"));
                Credential credential = user.getCredential();
                if (credential.getIsEnabled()) {
                    return Optional.of(credential);
                }
            }
        } catch (Exception e) {
            throw new ForbiddenException("Refresh token đã hết hạn hoặc không chính xác!");
        }
        return Optional.empty();
    }


    @Override
    public ResponseEntity<GenericResponse> resetPassword(EmailRequest emailRequest) {
        String email = emailRequest.getEmail();
        Optional<Credential> optionalCredential = credentialRepository.findByUsername(email);
        if (optionalCredential.isPresent() && optionalCredential.get().getIsEnabled()) {
            Credential credential = optionalCredential.get();
            emailService.sendResetPasswordEmail(credential);
            return ResponseEntity.ok()
                    .body(GenericResponse.builder()
                            .success(true)
                            .message("Vui lòng kiểm tra email để đặt lại mật khẩu!")
                            .result(null)
                            .statusCode(HttpStatus.OK.value())
                            .build());
        }
        throw new BadRequestException("Tài khoản không tồn tại hoặc chưa được xác thực!");
    }
}
