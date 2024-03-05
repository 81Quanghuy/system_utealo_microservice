package vn.iotstart.userservice.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import vn.iotstart.userservice.dto.LoginDTO;
import vn.iotstart.userservice.dto.request.RegisterRequest;
import vn.iotstart.userservice.dto.request.TokenRequest;
import vn.iotstart.userservice.dto.response.GenericResponse;
import vn.iotstart.userservice.entity.Account;
import vn.iotstart.userservice.repository.AccountRepository;
import vn.iotstart.userservice.repository.RefreshTokenRepository;
import vn.iotstart.userservice.security.JwtTokenProvider;
import vn.iotstart.userservice.security.UserDetail;
import vn.iotstart.userservice.service.AccountService;
import vn.iotstart.userservice.service.RefreshTokenService;

import java.util.*;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AccountService userService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    AccountRepository userRepository;

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {

        if (userService.findByEmail(loginDTO.getCredentialId()).isEmpty()
                && userService.findByPhone(loginDTO.getCredentialId()).isEmpty()) {
            return ResponseEntity.ok().body(GenericResponse.builder().success(false).message("not found user")
                    .result(null).statusCode(HttpStatus.NOT_FOUND.value()).build());
        }

        Optional<Account> optionalUser = userService.findByEmail(loginDTO.getCredentialId());
        if (optionalUser.isPresent() && !optionalUser.get().getIsVerified()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.builder().success(false).message("Your account is not verified!").result(null)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getCredentialId(), loginDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        String accessToken = jwtTokenProvider.generateAccessToken(userDetail);
        RefreshToken refreshToken = new RefreshToken();
        String token = jwtTokenProvider.generateRefreshToken(userDetail);
        refreshToken.setToken(token);
        refreshToken.setUser(userDetail.getUser().getUser());
        // invalid all refreshToken before
        refreshTokenService.revokeRefreshToken(userDetail.getUserId());
        refreshTokenService.save(refreshToken);
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", token);
        tokenMap.put("userId", userDetail.getUserId());
        tokenMap.put("roleName", userDetail.getUser().getUser().getRole().getRoleName().name());

        if (optionalUser.isPresent()) {
            optionalUser.get().setLastLoginAt(new Date());
            userService.save(optionalUser.get());
        }

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
            return userService.userRegister(registerRequest);
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader,
                                    @RequestParam("refreshToken") String refreshToken) {
        String accessToken = authorizationHeader.substring(7);

        if (jwtTokenProvider.getUserIdFromJwt(accessToken).equals(jwtTokenProvider.getUserIdFromJwt(refreshToken))) {
            return refreshTokenService.logout(refreshToken);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(GenericResponse.builder().success(false).message("Logout failed!")
                        .result("Please login before logout!").statusCode(HttpStatus.UNAUTHORIZED.value()).build());
    }

    @PostMapping("/logout-all")
    public ResponseEntity<GenericResponse> logoutAll(@RequestHeader("Authorization") String authorizationHeader,
                                                     @RequestParam("refreshToken") String refreshToken) {
        String accessToken = authorizationHeader.substring(7);
        if (jwtTokenProvider.getUserIdFromJwt(accessToken).equals(jwtTokenProvider.getUserIdFromJwt(refreshToken))) {
            String userId = jwtTokenProvider.getUserIdFromJwt(refreshToken);
            refreshTokenService.revokeRefreshToken(userId);
            SecurityContextHolder.clearContext();
            return ResponseEntity.ok().body(GenericResponse.builder().success(true).message("Logout successfully!")
                    .result("").statusCode(HttpStatus.OK.value()).build());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(GenericResponse.builder().success(false).message("Logout failed!")
                        .result("Please login before logout!").statusCode(HttpStatus.UNAUTHORIZED.value()).build());
    }

    @PostMapping("/refresh-access-token")
    public ResponseEntity<GenericResponse> refreshAccessToken(@RequestBody TokenRequest tokenRequest) {
        String refreshToken = tokenRequest.getRefreshToken();
        return refreshTokenService.refreshAccessToken(refreshToken);
    }

    @GetMapping(value = "/registration-confirm", produces = MediaType.TEXT_HTML_VALUE)
    public String confirmRegistration(@RequestParam("token") final String token) {
        String result = userService.validateVerificationAccount(token);
        Context context = new Context();
        context.setVariable("result", result);
        return templateEngine.process("result-confirm", context);
    }
}
