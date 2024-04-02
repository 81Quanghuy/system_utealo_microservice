package     vn.iostar.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.iostar.userservice.constant.TokenType;
import vn.iostar.userservice.dto.LoginDTO;
import vn.iostar.userservice.dto.request.RegisterRequest;
import vn.iostar.userservice.dto.request.TokenRequest;
import vn.iostar.userservice.dto.response.GenericResponse;
import vn.iostar.userservice.entity.Account;
import vn.iostar.userservice.entity.Token;
import vn.iostar.userservice.jwt.service.JwtService;
import vn.iostar.userservice.repository.AccountRepository;
import vn.iostar.userservice.repository.TokenRepository;
import vn.iostar.userservice.service.AccountService;
import vn.iostar.userservice.service.TokenService;


import java.util.*;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {
    public final PasswordEncoder passwordEncoder;
    public final AccountService accountService;
    public final TokenService tokenService;
    public final TokenRepository tokenRepository;
    public final AccountRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {

        if (accountService.findByEmail(loginDTO.getCredentialId()).isEmpty()
                && accountService.findByPhone(loginDTO.getCredentialId()).isEmpty()) {
            return ResponseEntity.ok().body(GenericResponse.builder().success(false).message("not found user")
                    .result(null).statusCode(HttpStatus.NOT_FOUND.value()).build());
        }

        Optional<Account> optionalUser = accountService.findByEmail(loginDTO.getCredentialId());
        if (optionalUser.isPresent() && !optionalUser.get().getIsVerified()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.builder().success(false).message("Your account is not verified!").result(null)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
        }

        String accessToken = jwtService.generateAccessToken(optionalUser.get());
        String refreshToken = jwtService.generateRefreshToken(optionalUser.get());

        Token token = Token.builder()
                .token(refreshToken)
                .isExpired(false)
                .isRevoked(false)
                .type(TokenType.REFRESH_ACCESS_TOKEN)
                .expiredAt(new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000))
                .user(optionalUser.get().getUser())
                .build();


        // Invalid all refreshToken before
        tokenService.revokeRefreshToken(optionalUser.get().getUser().getUserId());
        tokenService.save(token);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", refreshToken);
        tokenMap.put("userId", optionalUser.get().getUser().getUserId());
        tokenMap.put("roleName", optionalUser.get().getUser().getRole().getRoleName().name());

        optionalUser.get().setLastLoginAt(new Date());
        accountService.save(optionalUser.get());

        return ResponseEntity.ok().body(GenericResponse.builder().success(true).message("Login successfully!")
                .result(tokenMap).statusCode(HttpStatus.OK.value()).build());

    }

    @PostMapping("/register")
    public ResponseEntity<GenericResponse> registerProcess(@RequestBody @Valid RegisterRequest registerRequest,
                                                           BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();

            return ResponseEntity.status(500)
                    .body(new GenericResponse(false, errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
        } else {
            return accountService.userRegister(registerRequest);
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader,
                                    @RequestParam("refreshToken") String refreshToken) {
        String accessToken = authorizationHeader.substring(7);

        if (jwtService.extractUserId(accessToken).equals(jwtService.extractUserId(refreshToken))) {
            return tokenService.logout(refreshToken);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(GenericResponse.builder().success(false).message("Logout failed!")
                        .result("Please login before logout!").statusCode(HttpStatus.UNAUTHORIZED.value()).build());
    }
    @PostMapping("/refresh-access-token")
    public ResponseEntity<GenericResponse> refreshAccessToken(@RequestBody TokenRequest tokenRequest) {
        String refreshToken = tokenRequest.getRefreshToken();
        return tokenService.refreshAccessToken(refreshToken);
    }

}
