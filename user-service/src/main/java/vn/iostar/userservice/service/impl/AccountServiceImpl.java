package vn.iostar.userservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iostar.userservice.constant.KafkaTopicName;
import vn.iostar.userservice.dto.request.RegisterRequest;
import vn.iostar.userservice.dto.response.GenericResponse;
import vn.iostar.userservice.entity.*;
import vn.iostar.userservice.repository.AccountRepository;
import vn.iostar.userservice.repository.ProfileRepository;
import vn.iostar.userservice.repository.RoleRepository;
import vn.iostar.userservice.repository.UserRepository;
import vn.iostar.userservice.service.AccountService;
import vn.iostar.userservice.constant.RoleName;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public <S extends Account> List<S> saveAll(Iterable<S> entities) {
        return accountRepository.saveAll(entities);
    }

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TokenServiceImpl tokenService;

    private User userRegister;

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Transactional
    public ResponseEntity<GenericResponse> userRegister(RegisterRequest registerRequest) {
        if (registerRequest.getPassword().length() < 8 || registerRequest.getPassword().length() > 32)
            throw new RuntimeException("Password must be between 8 and 32 characters long");

        Optional<Account> userOptional = findByPhone(registerRequest.getPhone());
        if (userOptional.isPresent())
            return ResponseEntity.status(409)
                    .body(GenericResponse.builder().success(false).message("Phone number already in use").result(null)
                            .statusCode(HttpStatus.CONFLICT.value()).build());

        userOptional = findByEmail(registerRequest.getEmail());
        if (userOptional.isPresent())
            return ResponseEntity.status(409).body(GenericResponse.builder().success(false)
                    .message("Email already in use").result(null).statusCode(HttpStatus.CONFLICT.value()).build());

        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword()))
            return ResponseEntity.status(409)
                    .body(GenericResponse.builder().success(false).message("Password and confirm password do not match")
                            .result(null).statusCode(HttpStatus.CONFLICT.value()).build());

        Optional<Role> role = roleRepository.findByRoleName(RoleName.valueOf(registerRequest.getRoleName()));
        if (!role.isPresent()) {
            return ResponseEntity.status(404).body(GenericResponse.builder().success(false)
                    .message("Role name not found").result(null).statusCode(HttpStatus.CONFLICT.value()).build());
        }
        saveUserAndAccount(registerRequest, role.get());
//        if (registerRequest.getRoleName().equals(RoleName.SinhVien.name())) {
//            Optional<PostGroup> poOptional = postGroupRepository.findByPostGroupName(registerRequest.getGroupName());
//
//            if (!poOptional.isPresent() ) {
//                return ResponseEntity.status(409).body(GenericResponse.builder().success(false)
//                        .message("Group name not found").result(null).statusCode(HttpStatus.CONFLICT.value()).build());
//            }
//            saveGroupandRole(registerRequest, poOptional.get());
//        }
        kafkaTemplate.send(KafkaTopicName.USER_TOPIC, registerRequest.getEmail());

        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Sign Up Success").result(null)
                .statusCode(200).build());
    }

//    @Transactional
//    public void saveGroupandRole(RegisterRequest registerRequest, PostGroup postGroup) {
//
//        PostGroupMember postGroupMember = new PostGroupMember();
//        postGroupMember.setUser(userRegister);
//        postGroupMember.setRoleUserGroup(RoleUserGroup.Member);
//        postGroupMember.getPostGroup().add(postGroup);
//        postGroup.getPostGroupMembers().add(postGroupMember);
//
//        postGroupMemberRepository.save(postGroupMember);
//    }

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
