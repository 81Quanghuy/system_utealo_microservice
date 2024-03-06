package     vn.iostar.userservice.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import vn.iostar.userservice.dto.LoginDTO;
import vn.iostar.userservice.dto.request.RegisterRequest;
import vn.iostar.userservice.dto.request.TokenRequest;
import vn.iostar.userservice.dto.response.GenericResponse;
import vn.iostar.userservice.entity.Account;
import vn.iostar.userservice.entity.Token;
import vn.iostar.userservice.repository.AccountRepository;
import vn.iostar.userservice.repository.TokenRepository;
import vn.iostar.userservice.security.JwtTokenProvider;
import vn.iostar.userservice.security.UserDetail;
import vn.iostar.userservice.service.AccountService;
import vn.iostar.userservice.service.TokenService;


import javax.naming.Context;
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
    AccountService accountService;

    @Autowired
    TokenService tokenService;


    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    AccountRepository userRepository;

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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getCredentialId(), loginDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        String accessToken = jwtTokenProvider.generateAccessToken(userDetail);
        Token refreshToken = new Token();
        String token = jwtTokenProvider.generateRefreshToken(userDetail);
        refreshToken.setToken(token);
        refreshToken.setUser(userDetail.getUser().getUser());
        // invalid all refreshToken before
        tokenService.revokeRefreshToken(userDetail.getUserId());
        tokenService.save(refreshToken);
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", accessToken);
        tokenMap.put("refreshToken", token);
        tokenMap.put("userId", userDetail.getUserId());
        tokenMap.put("roleName", userDetail.getUser().getUser().getRole().getRoleName().name());

        if (optionalUser.isPresent()) {
            optionalUser.get().setLastLoginAt(new Date());
            accountService.save(optionalUser.get());
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
            return accountService.userRegister(registerRequest);
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader,
                                    @RequestParam("refreshToken") String refreshToken) {
        String accessToken = authorizationHeader.substring(7);

        if (jwtTokenProvider.getUserIdFromJwt(accessToken).equals(jwtTokenProvider.getUserIdFromJwt(refreshToken))) {
            return tokenService.logout(refreshToken);
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
            tokenService.revokeRefreshToken(userId);
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
        return tokenService.refreshAccessToken(refreshToken);
    }

}
