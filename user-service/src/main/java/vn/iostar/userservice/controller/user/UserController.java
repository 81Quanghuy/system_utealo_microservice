package vn.iostar.userservice.controller.user;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import vn.iostar.model.RelationshipResponse;
import vn.iostar.model.UserElastic;
import vn.iostar.userservice.dto.SearchUser;
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
import vn.iostar.userservice.model.UserDocument;
import vn.iostar.userservice.repository.elasticsearch.UsersElasticSearchRepository;
import vn.iostar.userservice.repository.jpa.UserRepository;
import vn.iostar.userservice.service.AccountService;
import vn.iostar.userservice.service.CloudinaryService;
import vn.iostar.userservice.service.UserService;
import vn.iostar.userservice.service.synchronization.UserSynchronizationService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {


    private final JwtService jwtService;

    private final UserService userService;

    private final CloudinaryService cloudinaryService;

    private final UserRepository userRepository;
    private final UserSynchronizationService userSynchronizationService;
    private final UsersElasticSearchRepository usersElasticSearchRepository;

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
        if (user.isEmpty()) {
            throw new RuntimeException("User not found.");
        } else {
            UserProfileResponse profileResponse = userService.getFullProfile(user.get());
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
    public ResponseEntity<GenericResponse> resetPassword(@RequestParam final String email)
            throws MessagingException, UnsupportedEncodingException {
        Optional<User> user = userService.findByAccountEmail(email);
        if (user.isEmpty()) {
            throw new RuntimeException("Người dùng không tồn tại!");
        }
        return userService.forgotPassword(user.get());
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
        return userService.getFullProfile(user.get());
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
    // get information of user by userId
    @GetMapping("/getProfileByUserId/{userId}")
    public UserProfileResponse getProfileByUserId(@PathVariable String userId) {
        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found.");
        }

        return new UserProfileResponse(user.get());
    }
    // cap nhat online cua user
    @PutMapping("/updateOnline")
    public ResponseEntity<GenericResponse> updateOnline(@RequestHeader("Authorization") String authorizationHeader,
        @RequestParam("isOnline") Boolean isOnline) {
        String token = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(token);
       return userService.updateOnline(userId,isOnline);
    }
  
    @GetMapping("/searchUser")
    public List<SearchUser> getUsersByName(@RequestParam("search") String search) {
        return userRepository.findUsersByName(search);
    }

    // lấy tất cả user
    @GetMapping("/getAllUserId")
    public List<String> getAllUserId() {
        return userService.getAllUserId();
    }

    //xac thuc tai khoan
    @PostMapping("/verify")
    ResponseEntity<GenericResponse> verifyUser(@RequestBody String email) {
        return userService.verifyUser(email);
    }
    // lay thong tin user la con cua user cha
    @GetMapping("/getChildren")
    public ResponseEntity<GenericResponse> getChildren(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(token);
        return userService.getChildren(userId);
    }
    // getParent
    @GetMapping("/getParent")
    public ResponseEntity<GenericResponse> getParent(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(token);
        return userService.getParent(userId);
    }
    // get parent chua xac thuc
    @GetMapping("/getParentNotVerify")
    public ResponseEntity<GenericResponse> getParentNotVerify(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(token);
        return userService.getParentNotVerify(userId);
    }

    // add relation ship
    @PostMapping("/addChildren")
    public ResponseEntity<GenericResponse> addRelationShip(@RequestHeader("Authorization") String authorizationHeader,
                                                           @RequestParam String email) {
        String token = authorizationHeader.substring(7);
        String currentId = jwtService.extractUserId(token);
        return userService.addRelationShip(currentId, email);
    }
    // lay thong tin children cua user
    @GetMapping("/getRelationShip")
    RelationshipResponse getRelationship(@RequestParam String currentId, @RequestParam String userId){
        return userService.getRelationship(currentId,userId);
    }

    // Search user by name, email, phone
    @GetMapping("/search")
    public List<UserElastic> searchUser(@RequestParam String key) throws IOException {
        return userSynchronizationService.autoSuggestUserSearch(key.trim());
    }

    //find all
    @GetMapping("/findAll")
    public Iterable<UserDocument> findAll() {
        return usersElasticSearchRepository.findAll();
    }

    //accept Parent
    @PutMapping("/acceptParent")
    public ResponseEntity<GenericResponse> acceptParent(@RequestHeader("Authorization") String authorizationHeader,
                                                        @RequestParam String parentId) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return userService.acceptParent(currentUserId, parentId);
    }

    // decline parent
    @DeleteMapping("/declineParent")
    public ResponseEntity<GenericResponse> declineParent(@RequestHeader("Authorization") String authorizationHeader,
                                                         @RequestParam String parentId) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return userService.declineParent(currentUserId, parentId);
    }
}