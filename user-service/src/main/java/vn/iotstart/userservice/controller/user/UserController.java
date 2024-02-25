package com.trvankiet.app.controller.user;

import com.trvankiet.app.dto.CredentialDto;
import com.trvankiet.app.dto.FriendRequestDto;
import com.trvankiet.app.dto.SimpleUserDto;
import com.trvankiet.app.dto.UserDto;
import com.trvankiet.app.dto.request.ChangePasswordRequest;
import com.trvankiet.app.dto.request.ProfileRequest;
import com.trvankiet.app.dto.response.FriendOfUserResponse;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.jwt.service.JwtService;
import com.trvankiet.app.service.UserService;
import com.trvankiet.app.service.client.FriendshipClientService;
import jakarta.validation.Valid;
import jakarta.ws.rs.PUT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = {"/api/v1/users"})
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final FriendshipClientService friendshipClientService;
    private final FriendRequestClientService friendRequestClientService;

    @GetMapping(value ="/credentials")
    public CredentialDto getCredentialDto(@RequestParam String uId) {
        log.info("AdminUserController Get, CredentialDto, getCredentialDto");
        return userService.getCredentialDto(uId);
    }

    @GetMapping(value ="/userDto/{uId}")
    public UserDto getUserDto(@PathVariable String uId) {
        log.info("AdminUserController Get, UserDto, getUserDto");
        return userService.getUserDetail(uId);
    }

    @GetMapping(value ="/profile")
    public ResponseEntity<GenericResponse> getUserProfile(@RequestHeader("Authorization") String authorizationHeader) {
        log.info("AdminUserController Get, GenericResponse, getUserProfile");
        String token = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(token);
        return userService.getUserProfile(userId);
    }

    @PutMapping(value ="/profile")
    public ResponseEntity<GenericResponse> updateProfile(@RequestHeader("Authorization") String authorizationHeader, @Valid @RequestBody ProfileRequest postProfileRequest) {
        log.info("AdminUserController Post, GenericResponse, updateProfile");
        String token = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(token);
        return userService.updateProfile(userId, postProfileRequest);
    }

    @PutMapping(value = "/profile/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse> updateAvatar(@RequestHeader("Authorization") String authorizationHeader, @RequestPart("avatar") MultipartFile avatar) throws IOException {
        log.info("AdminUserController Post, GenericResponse, updateAvatar");
        String token = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(token);
        return userService.updateAvatar(userId, avatar);
    }

    @PutMapping(value = "/profile/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse> updateCover(@RequestHeader("Authorization") String authorizationHeader, @RequestPart("cover") MultipartFile cover) throws IOException {
        log.info("AdminUserController Post, GenericResponse, updateCover");
        String token = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(token);
        return userService.updateCover(userId, cover);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List<SimpleUserDto>> searchUser(@RequestParam Optional<String> query
            , @RequestParam Optional<String> role
            , @RequestParam Optional<String> gender
            , @RequestParam Optional<String> school
            , @RequestParam Optional<Integer> grade
            , @RequestParam Optional<List<String>> subjects) {
        log.info("AdminUserController Get, UserDto, searchUser");
        return userService.searchUser(query, role, gender, school, grade, subjects);
    }

    @GetMapping(value ="/friends")
    public ResponseEntity<GenericResponse> getFriends(@RequestHeader("Authorization") String authorizationHeader) {
        log.info("AdminUserController Get, UserDto, getFriends");
        List<String> friendIds = friendshipClientService.getFriendIds(authorizationHeader).getBody();
        return userService.getFriends(friendIds);
    }
    @GetMapping(value ="/friend-requests")
    public ResponseEntity<GenericResponse> getFriendRequests(@RequestHeader("Authorization") String authorizationHeader) {
        log.info("AdminUserController Get, UserDto, getFriendRequests");
        List<FriendRequestDto> friendRequests = friendRequestClientService.getFriendRequests(authorizationHeader).getBody();
        return userService.getFriendRequests(friendRequests);
    }
    
    @GetMapping(value ="/friend-suggestions")
	public ResponseEntity<GenericResponse> getFriendSuggestions(
			@RequestHeader("Authorization") String authorizationHeader) {
		log.info("AdminUserController Get, UserDto, getFriendSuggestions");
		List<String> friendSuggestions = friendshipClientService.getFriendSuggestions(authorizationHeader).getBody();
		return userService.getFriendSuggestions(friendSuggestions);
	}

    @GetMapping(value ="/friends-of-user")
    public ResponseEntity<GenericResponse> getFriendsOfUser(@RequestHeader("Authorization") String authorizationHeader
                                                            ,@RequestParam String uId) {
        log.info("AdminUserController Get, UserDto, getFriendsOfUser");
        ResponseEntity<List<FriendOfUserResponse>> friendOfUserResponses = friendshipClientService.getFriendsOfUser(authorizationHeader, uId);

        return userService.getFriendsOfUser(friendOfUserResponses.getBody());
    }

    @GetMapping(value = "/simpleUserDto/{uId}")
    public SimpleUserDto getSimpleUserDto(@PathVariable String uId) {
        log.info("AdminUserController Get, SimpleUserDto, getSimpleUserDto");
        return userService.getSimpleUserDto(uId);
    }

    @PostMapping(value = "/change-password")
    public ResponseEntity<GenericResponse> changePassword(@RequestHeader("Authorization") String authorizationHeader
            , @RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        log.info("AdminUserController Post, ResponseEntity<GenericResponse>, changePassword");
        String token = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(token);
        return userService.changePassword(userId, changePasswordRequest);
    }

}
