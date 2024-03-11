package vn.iostar.friendservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import vn.iostar.friendservice.constant.KafkaTopicName;
import vn.iostar.friendservice.dto.FriendDTO;
import vn.iostar.friendservice.dto.FriendshipDto;
import vn.iostar.friendservice.dto.UserDto;
import vn.iostar.friendservice.dto.response.FriendOfUserResponse;
import vn.iostar.friendservice.dto.response.GenericResponse;
import vn.iostar.friendservice.dto.response.UserResponse;
import vn.iostar.friendservice.entity.Friend;
import vn.iostar.friendservice.jwt.service.JwtService;
import vn.iostar.friendservice.repository.FriendRepository;
import vn.iostar.friendservice.service.FriendService;
import vn.iostar.friendservice.service.impl.MessageServiceImpl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api/v1/friend")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;
    private final JwtService jwtService;
    private final FriendRepository friendRepository;
    private final KafkaTemplate<String, List<String>> kafkaTemplate;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MessageServiceImpl messageService;

    @GetMapping("/list/{userId}")
    public ResponseEntity<GenericResponse> getListFriendByUserId(@PathVariable("userId") String userId) {
        Optional<Friend> friend = friendRepository.findByAuthorId(userId);
        if (friend.isPresent()) {
            kafkaTemplate.send(KafkaTopicName.FRIEND_TOPIC, friend.get().getFriendIds());
            logger.info("Sent friend list to Kafka");
            List<UserResponse> result = messageService.getLastReceivedUser();
            if (!Objects.nonNull(result)) {
                return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get List Friend Successfully")
                        .result(result).statusCode(HttpStatus.OK.value()).build());
            }

        }
        return ResponseEntity.ok(GenericResponse.builder().success(false).message("Get List Friend Failed")
                .result(userId).statusCode(HttpStatus.OK.value()).build());

    }

    //create friends
    @PostMapping("/create-friend")
    public ResponseEntity<GenericResponse> createFriend(@RequestBody FriendshipDto friend) {
        log.info("FriendshipController, createFriend");

        return friendService.createFriend(friend);
    }

    @GetMapping
    public ResponseEntity<List<String>> getFriendIds(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.info("FriendshipController, getFriendIds");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return friendService.getFriendIds(userId);
    }

    @DeleteMapping
    public ResponseEntity<GenericResponse> deleteFriend(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestParam String friendId) {
        log.info("FriendshipController, deleteFriend");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return friendService.deleteFriend(userId, friendId);
    }

    @PostMapping
    public ResponseEntity<String> createFriendship(@RequestParam String userId) {
        log.info("FriendshipController, addFriend");
        return friendService.createFriendship(userId);
    }

    @GetMapping("/validate")
    public ResponseEntity<GenericResponse> validateFriendship(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestParam String friendId) {
        log.info("FriendshipController, validateFriendship");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return friendService.validateFriendship(userId, friendId);
    }

    @GetMapping("/list-friends")
    public ResponseEntity<List<FriendOfUserResponse>> getFriendsOfUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestParam String friendId) {
        log.info("FriendshipController, getFriendsOfUser");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return friendService.getFriendsOfUser(userId, friendId);
    }

    @GetMapping("/list-friend-suggestions")
    public ResponseEntity<List<String>> getFriendSuggestions(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        log.info("FriendshipController, getFriendSuggestions");
        String accessToken = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(accessToken);
        return friendService.getFriendSuggestions(userId);
    }
}
