package vn.iostar.friendservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.friendservice.dto.FriendRequestDto;
import vn.iostar.friendservice.dto.request.CreateFriendRequest;
import vn.iostar.friendservice.dto.response.FriendResponse;
import vn.iostar.friendservice.dto.response.GenericResponse;
import vn.iostar.friendservice.jwt.service.JwtService;
import vn.iostar.friendservice.service.FriendRequestService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friend/request")
@RequiredArgsConstructor
@Slf4j
public class FriendRequestController {

    private final JwtService jwtService;
    private final FriendRequestService friendRequestService;


    /**
     * Lay trang thai nguoi dung dua theo userId va userToken: Ban be, dang cho ket ban,chap nhan loi moi ket ban
     * @param authorizationHeader
     * @param userId
     * @return ResponseEntity<GenericResponse>
     */
    @GetMapping("/status/{userId}")
    public ResponseEntity<GenericResponse> getStatusByUserId(@RequestHeader("Authorization") String authorizationHeader,
                                                             @Valid @PathVariable("userId") String userId) {
        String token = authorizationHeader.substring(7);
        String userIdToken = jwtService.extractUserId(token);
        return friendRequestService.getStatusByUserId(userId,userIdToken);
    }

    /**
     * Lấy danh sách những user mình đã gửi lời mời kết bạn
     *
     * @param authorizationHeader The JWT (JSON Web Token) provided in the "Authorization"
     *                      header for authentication.
     * @return The resource if found, or a 404 Not Found response.
     */
    @GetMapping("/list")
    public ResponseEntity<GenericResponse> getRequestList(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(token);
        return friendRequestService.getRequestList(userId);
    }

    /**
     * Lấy danh sách những user mình đã gửi lời mời kết bạn có sử dụng phân trang
     *
     * @param authorizationHeader The JWT (JSON Web Token) provided in the "Authorization"
     *                      header for authentication.
     * @return The resource if found, or a 404 Not Found response.
     */
    @GetMapping("/list/pageable")
    public ResponseEntity<GenericResponse> getSenderRequestPageable(
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(token);
        return friendRequestService.getSenderRequestPageable(userId);
    }

    /**
     * Gui loi moi ket ban
     *
     * @param authorizationHeader The JWT (JSON Web Token) provided in the "Authorization"
     *                      header for authentication.
     * @return The resource if found, or a 404 Not Found response.
     */
    @PostMapping("/send/{userId}")
    public ResponseEntity<GenericResponse> sendFriendRequest(@RequestHeader("Authorization") String authorizationHeader,
                                                             @PathVariable("userId") String userId) {
        String token = authorizationHeader.substring(7);
        String userIdToken = jwtService.extractUserId(token);
        return friendRequestService.sendFriendRequest(userId, userIdToken);
    }

    /**
     * Lấy danh sách người dùng đã gửi lời mời kết bạn
     *
     * @param authorizationHeader The JWT (JSON Web Token) provided in the "Authorization"
     *                      header for authentication.
     * @return The resource if found, or a 404 Not Found response.
     */
    @GetMapping("/requestFrom/list")
    public ResponseEntity<GenericResponse> getInvitationSenderList(
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(token);
        return friendRequestService.getInvitationSenderList(userId);
    }

    /**
     * DELETE  Từ chối lời mời kết bạn by Authorization and userId
     *
     * @param userId
     * @param authorizationHeader The JWT (JSON Web Token) provided in the "Authorization"
     *                      header for authentication.
     * @return The resource if found, or a 404 Not Found response.
     */
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<GenericResponse> deleteFriendRequest(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @Valid @PathVariable("userId") String userId) {
        String token = authorizationHeader.substring(7);
        String userIdToken = jwtService.extractUserId(token);
        return friendRequestService.deleteFriendRequest(userIdToken,userId);
    }

    /**
     * DELETE hủy lời mời kết bạn by Authorization and userId
     *
     * @param userId
     * @param authorizationHeader The JWT (JSON Web Token) provided in the "Authorization"
     *                      header for authentication.
     * @return The resource if found, or a 404 Not Found response.
     */
    @DeleteMapping("/cancel/{userId}")
    public ResponseEntity<GenericResponse> cancelRequestFriend(
            @RequestHeader("Authorization") String authorizationHeader, @Valid @PathVariable("userId") String userId) {
        String token = authorizationHeader.substring(7);
        String userIdToken = jwtService.extractUserId(token);
        return friendRequestService.cancelRequestFriend(userIdToken,userId);
    }

    /**
     * PUT accept FriendRequest by Authorization and userId
     *
     * @param userId
     * @param authorizationHeader The JWT (JSON Web Token) provided in the "Authorization"
     *                      header for authentication.
     * @return The resource if found, or a 404 Not Found response.
     */
    @PutMapping("/accept/{userId}")
    public ResponseEntity<GenericResponse> updateUser(@RequestHeader("Authorization") String authorizationHeader,
                                                      @Valid @PathVariable("userId") String userId) {
        String token = authorizationHeader.substring(7);
        String userIdToken = jwtService.extractUserId(token);
        return friendRequestService.acceptRequest(userIdToken,userId);

    }
}
