package vn.iostar.friendservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.friendservice.dto.FriendshipDto;
import vn.iostar.friendservice.dto.response.FriendOfUserResponse;
import vn.iostar.friendservice.dto.response.GenericResponse;
import vn.iostar.friendservice.jwt.service.JwtService;
import vn.iostar.friendservice.service.FriendService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;
    private final JwtService jwtService;


    //create friends
    @PostMapping("/create-friend")
    public ResponseEntity<GenericResponse> createFriend( @RequestBody FriendshipDto friend){
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
