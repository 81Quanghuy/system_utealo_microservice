package vn.iostar.userservice.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import vn.iostar.userservice.constant.KafkaTopicName;
import vn.iostar.userservice.converters.UserMessageConverter;
import vn.iostar.userservice.dto.UserDto;
import vn.iostar.userservice.dto.response.GenericResponse;
import vn.iostar.userservice.dto.response.UserProfileResponse;
import vn.iostar.userservice.entity.User;
import vn.iostar.userservice.security.JwtTokenProvider;
import vn.iostar.userservice.service.UserService;


import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;
    @GetMapping("/profile/{userId}")
    public ResponseEntity<GenericResponse> getInformation(@RequestHeader("Authorization") String authorizationHeader,
                                                          @PathVariable("userId") String userId) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);

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

    @GetMapping("/getUserId")
    public String getUserId(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
        return currentUserId;
    }

    @PostMapping
    public String sendUser(@RequestParam String email) {
        kafkaTemplate.send(KafkaTopicName.USER_TOPIC, email);
        return "User sent successfully!"+email;
    }
}