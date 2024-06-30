package     vn.iostar.userservice.controller;

import jakarta.validation.Valid;
import lombok.Getter;
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
import vn.iostar.userservice.dto.request.EmailRequest;
import vn.iostar.userservice.dto.request.RegisterRequest;
import vn.iostar.userservice.dto.request.TokenRequest;
import vn.iostar.userservice.dto.response.GenericResponse;
import vn.iostar.userservice.dto.response.UserResponse;
import vn.iostar.userservice.entity.Account;
import vn.iostar.userservice.entity.Profile;
import vn.iostar.userservice.entity.Token;
import vn.iostar.userservice.entity.User;
import vn.iostar.userservice.exception.wrapper.BadRequestException;
import vn.iostar.userservice.jwt.service.JwtService;
import vn.iostar.userservice.repository.AccountRepository;
import vn.iostar.userservice.repository.ProfileRepository;
import vn.iostar.userservice.repository.TokenRepository;
import vn.iostar.userservice.repository.UserRepository;
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
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;


    @PostMapping("/login")
    @Transactional
    public ResponseEntity<GenericResponse> login(@Valid @RequestBody LoginDTO loginDTO) {
       return accountService.login(loginDTO);
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

    @PostMapping("/sendOTP")
    public ResponseEntity<GenericResponse> sendOTP(@RequestBody EmailRequest email) {
        return accountService.sendOTP(email.getEmail());
    }

    @PostMapping("/verifyParent")
    public ResponseEntity<GenericResponse> verifyParent(@RequestParam String token) {
        return accountService.verifyParent(token);
    }
}
