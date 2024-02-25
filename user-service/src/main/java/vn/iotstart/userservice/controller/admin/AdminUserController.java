package com.trvankiet.app.controller.admin;

import com.trvankiet.app.dto.request.BanUserRequest;
import com.trvankiet.app.dto.request.UnbanUserRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/get-all-users")
    public ResponseEntity<GenericResponse> getAllUsers(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("AdminUserController, getAllUsers");
        return userService.getAllUsers(authorizationHeader, page, size);
    }

    @PostMapping("/ban-user")
    public ResponseEntity<GenericResponse> banUser(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestBody BanUserRequest banUserRequest) {
        log.info("AdminUserController, banUser");
        return userService.banUser(authorizationHeader, banUserRequest);
    }

    @PostMapping("/unban-user")
    public ResponseEntity<GenericResponse> unbanUser(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestBody UnbanUserRequest unbanRequest) {
        log.info("AdminUserController, unbanUser");
        return userService.unbanUser(authorizationHeader, unbanRequest);
    }

}
