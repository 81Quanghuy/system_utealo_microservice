package com.trvankiet.app.service;

import com.trvankiet.app.dto.CredentialDto;
import com.trvankiet.app.dto.FriendRequestDto;
import com.trvankiet.app.dto.SimpleUserDto;
import com.trvankiet.app.dto.UserDto;
import com.trvankiet.app.dto.request.*;
import com.trvankiet.app.dto.response.FriendOfUserResponse;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    <S extends User> S save(S entity);

    List<User> findAll();

    Optional<User> findById(String id);

    boolean existsById(String id);

    long count();

    void deleteById(String id);

    void delete(User entity);

    ResponseEntity<GenericResponse> initCredentialInfo(String token, UserInfoRequest userInfoRequest);

    CredentialDto getCredentialDto(String uId);

    UserDto getUserDetail(String uId);

    ResponseEntity<GenericResponse> getUserProfile(String userId);

    ResponseEntity<GenericResponse> updateProfile(String userId, ProfileRequest postProfileRequest);

    ResponseEntity<GenericResponse> updateAvatar(String userId, MultipartFile avatar) throws IOException;

    ResponseEntity<GenericResponse> updateCover(String userId, MultipartFile cover) throws IOException;

    ResponseEntity<List<SimpleUserDto>> searchUser(Optional<String> query, Optional<String> role, Optional<String> gender, Optional<String> school, Optional<Integer> grade, Optional<List<String>> subjects);

    ResponseEntity<GenericResponse> getFriends(List<String> friendIds);

    ResponseEntity<GenericResponse> getFriendRequests(List<FriendRequestDto> friendRequests);

    ResponseEntity<GenericResponse> getFriendsOfUser(List<FriendOfUserResponse> body);

    SimpleUserDto getSimpleUserDto(String uId);

    ResponseEntity<GenericResponse> getAllUsers(String authorizationHeader, Integer page, Integer size);

    ResponseEntity<GenericResponse> changePassword(String userId, ChangePasswordRequest changePasswordRequest);

    ResponseEntity<GenericResponse> banUser(String authorizationHeader, BanUserRequest banUserRequest);

    ResponseEntity<GenericResponse> unbanUser(String authorizationHeader, UnbanUserRequest unbanRequest);

	ResponseEntity<GenericResponse> getFriendSuggestions(List<String> friendSuggestions);
}
