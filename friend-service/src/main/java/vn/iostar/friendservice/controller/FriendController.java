package vn.iostar.friendservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.friendservice.dto.response.FriendResponse;
import vn.iostar.friendservice.dto.response.GenericResponse;
import vn.iostar.friendservice.jwt.service.JwtService;
import vn.iostar.friendservice.service.FriendService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/friend")
@RequiredArgsConstructor
public class FriendController {
    private final FriendService friendService;
    private final JwtService jwtService;

    /**
     * Lay danh sach ban be theo userId
     *
     * @return ResponseEntity<GenericResponse>
     */
    @GetMapping("/list/{userId}")
    public ResponseEntity<GenericResponse> getListFriendByUserId(@PathVariable("userId") String userId) {
        List<FriendResponse> friend = friendService.findFriendUserIdsByUserId(userId);
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get List Friend Successfully")
                .result(friend).statusCode(HttpStatus.OK.value()).build());
    }


    //Laays danh saach ban be theo userId co su dung phan trang
    @GetMapping("/list/pageable/{userId}")
    public ResponseEntity<GenericResponse> getListFriendByUserId(@PathVariable("userId") String userId,
                                                                 //default page = 0, size = 10
                                                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                                                 @RequestParam(value = "size" , defaultValue = "5") int size) {
        List<FriendResponse> friend = friendService.findFriendUserIdsByUserIdPageable(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get List Friend Successfully")
                .result(friend).statusCode(HttpStatus.OK.value()).build());
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<GenericResponse> deleteFriend(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @PathVariable("userId") String userId) {
        log.info("FriendshipController, deleteFriend");
        String accessToken = authorizationHeader.substring(7);
        String userIdToken = jwtService.extractUserId(accessToken);
        return friendService.deleteFriend(userIdToken, userId);
    }


    /**
     * GET list FriendRequest by Authorization
     *
     * @param authorizationHeader The JWT (JSON Web Token) provided in the "Authorization"
     *                            header for authentication.
     * @return The resource if found, or a 404 Not Found response.
     */
    @GetMapping("/suggestion/list")
    public ResponseEntity<GenericResponse> getSuggestionList(
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String userIdToken = jwtService.extractUserId(token);
        return friendService.getFriendSuggestions(userIdToken);
    }

}
