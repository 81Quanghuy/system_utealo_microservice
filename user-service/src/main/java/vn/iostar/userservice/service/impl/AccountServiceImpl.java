package vn.iostar.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iostar.userservice.constant.KafkaTopicName;
import vn.iostar.userservice.constant.RoleName;
import vn.iostar.userservice.dto.LoginDTO;
import vn.iostar.userservice.dto.request.RegisterRequest;
import vn.iostar.userservice.dto.response.GenericResponse;
import vn.iostar.userservice.entity.*;
import vn.iostar.userservice.exception.wrapper.BadRequestException;
import vn.iostar.userservice.jwt.service.JwtService;
import vn.iostar.userservice.repository.AccountRepository;
import vn.iostar.userservice.repository.ProfileRepository;
import vn.iostar.userservice.repository.RoleRepository;
import vn.iostar.userservice.repository.UserRepository;
import vn.iostar.userservice.service.AccountService;
import vn.iostar.userservice.service.TokenService;
import vn.iostar.userservice.constant.TokenType;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final JwtService jwtService;
    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final ProfileRepository profileRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private  User userRegister;

    @Override
    public <S extends Account> List<S> saveAll(Iterable<S> entities) {
        return accountRepository.saveAll(entities);
    }

    @Override
    public ResponseEntity<GenericResponse> login(LoginDTO loginDTO) {
        Optional<Account> optionalUser = findByEmail(loginDTO.getCredentialId());
        if (optionalUser.isEmpty())
            throw new BadRequestException("Tài khoản hoặc mật khẩu không đúng!");
        else {
            if (!passwordEncoder.matches(loginDTO.getPassword(), optionalUser.get().getPassword()))
                throw new BadRequestException("Tài khoản hoặc mật khẩu không đúng!");
        }
        if (!optionalUser.get().getIsVerified()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.builder().success(false).message("Tài khoản chưa được kích họat!!!").result(null)
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
        save(optionalUser.get());

        return ResponseEntity.ok().body(GenericResponse.builder().success(true).message("Login successfully!")
                .result(tokenMap).statusCode(HttpStatus.OK.value()).build());
    }
    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Transactional
    public ResponseEntity<GenericResponse> userRegister(RegisterRequest registerRequest) {

        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword()))
            return ResponseEntity.status(409)
                    .body(GenericResponse.builder().success(false).message("Password and confirm password do not match")
                            .result(null).statusCode(HttpStatus.CONFLICT.value()).build());

        if (registerRequest.getPassword().length() < 8 || registerRequest.getPassword().length() > 32)
            throw new RuntimeException("Mật khẩu phải từ 8 đến 32 ký tự!");
        Optional<Account> userOptional = findByPhone(registerRequest.getPhone());
        if (userOptional.isPresent())
            return ResponseEntity.status(409)
                    .body(GenericResponse.builder().success(false).message("Số điện thoại này đã được sử dụng!").result(null)
                            .statusCode(HttpStatus.CONFLICT.value()).build());

        Optional<Account> accountOptional = findByEmail(registerRequest.getEmail());
        if (accountOptional.isPresent())
            return ResponseEntity.status(409).body(GenericResponse.builder().success(false)
                    .message("Email này đã được sử dụng!!").result(null).statusCode(HttpStatus.CONFLICT.value()).build());

        Optional<Role> role = roleRepository.findByRoleName(RoleName.valueOf(registerRequest.getRoleName()));
        if (role.isEmpty()) {
            return ResponseEntity.status(404).body(GenericResponse.builder().success(false)
                    .message("Role name not found").result(null).statusCode(HttpStatus.CONFLICT.value()).build());
        }
        saveUserAndAccount(registerRequest, role.get());
        kafkaTemplate.send(KafkaTopicName.EMAIL_REGISTER_TOPIC, registerRequest.getEmail());
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Đăng ký thành công!").result(registerRequest)
                .statusCode(200).build());
    }
    public <S extends Account> S save(S entity) {
        return accountRepository.save(entity);
    }

    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public Optional<Account> findByPhone(String phone) {
        return accountRepository.findByPhone(phone);
    }

    @Override
    public String validateVerificationAccount(String token) {
//        Token verificationToken = tokenService.findByToken(token);
//        if (verificationToken == null) {
//            return "Invalid token, please check the token again!";
//        }
//        User user = verificationToken.getUser();
//        user.setVerified(true);
//        userRepository.save(user);
//        return "Account verification successful, please login!";
        return null;
    }

    public void saveUserAndAccount(RegisterRequest registerRequest, Role role) {

        User user = new User();
        user.setPhone(registerRequest.getPhone());
        user.setUserName(registerRequest.getFullName());
        user.setRole(role);
        user.setGender(registerRequest.getGender());
        userRegister = user;
        userRepository.save(user);

        Account account = new Account();
        account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        account.setPhone(registerRequest.getPhone());
        account.setEmail(registerRequest.getEmail());

        Date createDate = new Date();
        account.setCreatedAt(createDate);
        account.setUpdatedAt(createDate);

        account.setUser(user);
        accountRepository.save(account);

        Profile profile = new Profile();
        profile.setUser(user);
        profileRepository.save(profile);

    }
}
