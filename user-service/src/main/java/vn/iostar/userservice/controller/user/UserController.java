package vn.iostar.userservice.controller.user;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.RequiredArgsConstructor;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.userservice.constant.KafkaTopicName;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import vn.iostar.userservice.dto.UserIds;
import vn.iostar.userservice.dto.request.ChangePasswordRequest;
import vn.iostar.userservice.dto.request.PasswordResetRequest;
import vn.iostar.userservice.dto.request.UpdateIsActiveRequest;
import vn.iostar.userservice.dto.request.UserUpdateRequest;
import vn.iostar.userservice.dto.response.FriendResponse;
import vn.iostar.userservice.dto.response.GenericResponse;
import vn.iostar.userservice.dto.response.UserProfileResponse;
import vn.iostar.userservice.entity.PasswordResetOtp;
import vn.iostar.userservice.entity.User;
import vn.iostar.userservice.jwt.service.JwtService;
import vn.iostar.userservice.service.AccountService;
import vn.iostar.userservice.service.CloudinaryService;
import vn.iostar.userservice.service.UserService;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {


    private final JwtService jwtService;

    private final UserService userService;

    @Autowired
    CloudinaryService cloudinaryService;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    AccountService accountService;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    Environment env;

    @GetMapping("/home")
    public String homePage() {
        return "Hello User";
    }

    @GetMapping("/profile")
    public ResponseEntity<GenericResponse> getInformation(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(token);
        return userService.getProfile(userId);
    }

    @GetMapping("/avatarAndName/{userId}")
    public ResponseEntity<GenericResponse> getAvatarAndName(@PathVariable("userId") String userId) {
        return userService.getAvatarAndName(userId);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<GenericResponse> getInformation(@RequestHeader("Authorization") String authorizationHeader,
                                                          @PathVariable("userId") String userId) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);

        Optional<User> user = userService.findById(userId);
        Pageable pageable = PageRequest.of(0, 5);
        UserProfileResponse profileResponse = userService.getFullProfile(user, pageable);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found.");
        } else {
            return ResponseEntity.ok(GenericResponse.builder().success(true).message("Successfully")
                    .result(profileResponse).statusCode(HttpStatus.OK.value()).build());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateUser(@RequestBody @Valid UserUpdateRequest request,
                                             @RequestHeader("Authorization") String authorizationHeader, BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new Exception(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }

        String token = authorizationHeader.substring(7);
        String userIdFromToken = jwtService.extractUserId(token);

        return userService.updateProfile(userIdFromToken, request);

    }

    @PutMapping("/change-password")
    public ResponseEntity<GenericResponse> changePassword(@RequestBody @Valid ChangePasswordRequest request,
                                                          @RequestHeader("Authorization") String authorizationHeader, BindingResult bindingResult) throws Exception {
        String token = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(token);
        if (bindingResult.hasErrors()) {
            throw new RuntimeException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }

        return userService.changePassword(userId, request);
    }

    @PostMapping("/forgot-password")
    public GenericResponse resetPassword(@RequestParam final String email)
            throws MessagingException, UnsupportedEncodingException {
        Optional<User> user = userService.findByAccountEmail(email);
        if (user.isEmpty()) {
            return GenericResponse.builder().success(false).message("NOT FOUND").result("Not found Email")
                    .statusCode(HttpStatus.NOT_FOUND.value()).build();
        }

        String otp = UUID.randomUUID().toString();
        userService.createPasswordResetOtpForUser(user.get(), otp);
        String url = "http://localhost:8000/reset-password?token=" + otp;
        String subject = "Thay đổi mật khẩu tài khoản UteAlo";
        Context context = new Context();
        context.setVariable("url", url);
        String content = templateEngine.process("forgot-password", context);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setSubject(subject);
        helper.setText(content, true);
        helper.setTo(user.get().getAccount().getEmail());
        helper.setFrom(Objects.requireNonNull(env.getProperty("spring.mail.username")), "Admin UteAlo");

        javaMailSender.send(message);

        return GenericResponse.builder().success(true).message("Please check your email to reset your password!")
                .result("Send Otp successfully!").statusCode(HttpStatus.OK.value()).build();
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token,
                                           @Valid @RequestBody PasswordResetRequest passwordResetRequest) {
        String result = userService.validatePasswordResetOtp(token);
        if (result == null) {
            Optional<PasswordResetOtp> user = userService.getUserByPasswordResetOtp(token);
            if (user.isEmpty()) {
                return ResponseEntity.ok(GenericResponse.builder().success(true).message("Không tìm thấy người dùng").result(null)
                        .statusCode(404).build());
            }
            userService.changeUserPassword(user.get().getUser(), passwordResetRequest.getNewPassword(),
                    passwordResetRequest.getConfirmPassword());
            return ResponseEntity.ok(GenericResponse.builder().success(true).message("Đặt lại mật khẩu thành công")
                    .result(null).statusCode(200).build());
        }
        return new ResponseEntity<Object>(GenericResponse.builder().success(false).message(result).result(null)
                .statusCode(HttpStatus.BAD_REQUEST.value()).build(), HttpStatus.BAD_REQUEST);

    }

    @PutMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam MultipartFile imageFile,
                                          @RequestHeader("Authorization") String token) throws IOException {

        String jwt = token.substring(7);
        String userId = jwtService.extractUserId(jwt);

        User user = userService.findById(userId).get();
        String avatarOld = user.getProfile().getAvatar();

        // upload new avatar
        user.getProfile().setAvatar(cloudinaryService.uploadImage(imageFile));
        userService.save(user);

        // delete old avatar
        if (avatarOld != null) {
            cloudinaryService.deleteImage(avatarOld);
        }
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Upload successfully")
                .result(user.getProfile().getAvatar()).statusCode(HttpStatus.OK.value()).build());
    }

    @PutMapping("/background")
    public ResponseEntity<?> uploadBackgroundPicture(@RequestParam MultipartFile imageFile,
                                                     @RequestHeader("Authorization") String token) throws IOException {

        String jwt = token.substring(7);
        String userId = jwtService.extractUserId(jwt);

        User user = userService.findById(userId).get();
        String backgroundOld = user.getProfile().getBackground();

        // upload new avatar
        user.getProfile().setBackground(cloudinaryService.uploadImage(imageFile));
        userService.save(user);

        // delete old avatar
        if (backgroundOld != null) {
            cloudinaryService.deleteImage(backgroundOld);
        }
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Upload successfully")
                .result(user.getProfile().getAvatar()).statusCode(HttpStatus.OK.value()).build());
    }

    @PutMapping("/delete")
    public ResponseEntity<GenericResponse> deleteUser(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String userIdFromToken = jwtService.extractUserId(token);
        return userService.deleteUser(userIdFromToken);

    }

    // Tìm kiếm bài viết, user, nhóm
//    @GetMapping("/search/key")
//    public ResponseEntity<GenericResponse> searchPostGroups(@RequestHeader("Authorization") String authorizationHeader,
//                                                            @RequestParam("search") String search) {
//        String token = authorizationHeader.substring(7);
//        String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
//        return groupService.searchGroupAndUserContainingIgnoreCase(search, userIdToken);
//    }

    @GetMapping("/getIsActiveOfUser/{userId}")
    public boolean getIsActiveOfUser(@PathVariable("userId") String userId) {
        Optional<User> user = userService.findById(userId);
        return user.get().getIsActive();
    }

    @GetMapping("/getIsActive")
    public boolean getIsActive(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String userIdToken = jwtService.extractUserId(token);
        Optional<User> user = userService.findById(userIdToken);
        return user.get().getIsActive();
    }

    @PutMapping("/updateIsActive")
    public ResponseEntity<String> updateIsActive(@RequestHeader("Authorization") String authorizationHeader,
                                                 @ModelAttribute UpdateIsActiveRequest request) {
        try {
            String token = authorizationHeader.substring(7);
            String userIdToken = jwtService.extractUserId(token);
            Optional<User> userOP = userService.findById(userIdToken);
            // Lấy trạng thái isActive từ request body
            Boolean isActive = request.getIsActive();

            if (userOP.isPresent()) {
                User user = userOP.get();
                // Cập nhật trạng thái isActive cho người dùng
                user.setIsActive(isActive);
                userService.save(user); // Lưu thay đổi vào cơ sở dữ liệu
                return ResponseEntity.ok("Cập nhật trạng thái isActive thành công!");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Đã xảy ra lỗi khi cập nhật trạng thái isActive");
        }
    }

    // UserClient
    @GetMapping("/getUser/{userId}")
    public UserProfileResponse getUser(@PathVariable String userId) {

        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found.");
        }
        Pageable pageable = PageRequest.of(0, 5);
        UserProfileResponse profileResponse = userService.getFullProfile(user, pageable);
        return profileResponse;
    }


    // UserClient
    @GetMapping("/getUserId")
    public String getUserId(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        return jwtService.extractUserId(token);
    }

    /**
     * GET FriendResponse by List userId
     * @param list_userId
     * @return FriendResponse: userId, name, avatar, background
     */
    @PostMapping("/getProfileByListUserId")
    public List<FriendResponse> getFriendByListUserId(@RequestBody UserIds list_userId) {
        return userService.getFriendByListUserId(list_userId);
    }
}