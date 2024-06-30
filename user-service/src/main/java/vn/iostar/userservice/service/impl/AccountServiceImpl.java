package vn.iostar.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iostar.constant.KafkaTopicName;
import vn.iostar.model.VerifyParent;
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
import vn.iostar.userservice.service.RelationshipService;
import vn.iostar.userservice.service.TokenService;
import vn.iostar.userservice.constant.TokenType;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final RelationshipService relationshipService;

    private final UserRepository userRepository;

    private final ProfileRepository profileRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTemplate<String, VerifyParent> kafkaTemplateVerify;

    @Override
    public <S extends Account> void saveAll(Iterable<S> entities) {
        accountRepository.saveAll(entities);
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
        if (!optionalUser.get().getIsActive()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.builder().success(false).message("Tài khoản đã bị khóa!!!").result(null)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
        }
        if(optionalUser.get().getUser().getRole().getRoleName().equals(RoleName.PhuHuynh)){
            if(!optionalUser.get().getUser().getParent().isEmpty()){
                // kiểm tra bảng relationship xem có dòng nào được chấp nhận không
                boolean isAccepted = false;
                for(Relationship relationship: optionalUser.get().getUser().getParent()){
                    if(relationship.getIsAccepted()){
                        isAccepted = true;
                        break;
                    }
                }
                if(!isAccepted){
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(GenericResponse.builder().success(false).message("Tài khoản chưa được xác thực bởi sinh viên !!!").result(null)
                                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
                }
            }
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
    public ResponseEntity<GenericResponse> sendOTP(String email) {
        kafkaTemplate.send(KafkaTopicName.EMAIL_REGISTER_TOPIC, email);
        return ResponseEntity.ok().body(GenericResponse.builder().success(true).message("Send OTP successfully!")
                .result(null).statusCode(HttpStatus.OK.value()).build());
    }

    @Override
    public ResponseEntity<GenericResponse> verifyParent(String token) {
        System.out.println("token: "+token);
        Optional<Token> tokenOptional = tokenService.findByToken(token);
        if (tokenOptional.isEmpty())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.builder().success(false).message("Token không tồn tại!!!").result(null)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
        if (tokenOptional.get().getIsExpired())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.builder().success(false).message("Token đã hết hạn!!!").result(null)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
        if (tokenOptional.get().getIsRevoked())
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GenericResponse.builder().success(false).message("Token đã bị thu hồi!!!").result(null)
                            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
        tokenOptional.get().setIsRevoked(true);
        tokenService.save(tokenOptional.get());
        Account account = tokenOptional.get().getUser().getAccount();
        Optional<Relationship> relationship = relationshipService.findByParent(account.getUser().getUserId());
        if(relationship.isPresent()){
            relationship.get().setIsAccepted(true);
            relationshipService.save(relationship.get());
        }
        save(account);
        return ResponseEntity.ok().body(GenericResponse.builder().success(true).message("Xác thực thành công!")
                .result(null).statusCode(HttpStatus.OK.value()).build());
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

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
        Optional<Account> accountOptionalStudent = findByEmail(registerRequest.getEmailStudent());
        if(registerRequest.getRoleName().equals(RoleName.PhuHuynh.toString())){
            if (accountOptionalStudent.isEmpty())
                return ResponseEntity.status(409).body(GenericResponse.builder().success(false)
                        .message("Email của học sinh không tồn tại!").result(null).statusCode(HttpStatus.CONFLICT.value()).build());
        }
        if (role.isEmpty()) {
            return ResponseEntity.status(404).body(GenericResponse.builder().success(false)
                    .message("Role name not found").result(null).statusCode(HttpStatus.CONFLICT.value()).build());
        }
        saveUserAndAccount(registerRequest, role.get(),accountOptionalStudent);
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
    public void saveUserAndAccount( RegisterRequest registerRequest, Role role, Optional<Account> accountOptionalStudent) {

        User user = new User();
        user.setPhone(registerRequest.getPhone());
        user.setUserName(registerRequest.getFullName());
        user.setRole(role);
        user.setGender(registerRequest.getGender());
        user.setIsActive(true);
        userRepository.save(user);

        if(registerRequest.getRoleName().equals(RoleName.PhuHuynh.toString())){
            if(accountOptionalStudent.isPresent()){
                Relationship relationship = new Relationship();
                relationship.setChild(accountOptionalStudent.get().getUser());
                relationship.setParent(user);
                relationshipService.save(relationship);
                List<Relationship> relationships = new ArrayList<>();
                relationships.add(relationship);
                user.setChild(relationships);
                userRepository.save(user);
            }
        }
        Account account = new Account();
        account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        account.setPhone(registerRequest.getPhone());
        account.setEmail(registerRequest.getEmail());
        if(registerRequest.getRoleName().equals(RoleName.PhuHuynh.toString())){
            Token token = new Token();
            token.setUser(user);
            token.setToken(UUID.randomUUID().toString());
            token.setIsExpired(false);
            token.setIsRevoked(false);
            token.setType(TokenType.VERIFICATION_TOKEN);
            token.setExpiredAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
            tokenService.save(token);
            VerifyParent verifyParent = new VerifyParent(registerRequest.getEmailStudent(),registerRequest.getEmail(), registerRequest.getFullName(), token.getToken());
            kafkaTemplateVerify.send(KafkaTopicName.EMAIL_VERIFY_TOPIC, verifyParent);
        }

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
