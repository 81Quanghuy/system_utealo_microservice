package vn.iostar.friendservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.friendservice.dto.response.FriendResponse;
import vn.iostar.friendservice.dto.response.GenericResponse;
import vn.iostar.friendservice.entity.Friend;
import vn.iostar.friendservice.entity.FriendRequest;
import vn.iostar.friendservice.jwt.service.JwtService;
import vn.iostar.friendservice.repository.FriendRepository;
import vn.iostar.friendservice.repository.FriendRequestRepository;
import vn.iostar.friendservice.service.FriendService;

import java.util.Map;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api/v1/friend")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;
    private final JwtService jwtService;
    private final FriendRepository friendRepository;
    private final FriendRequestRepository friendRequestRepository;

    /**
     * Lay danh sach ban be theo userId
     *
     * @return ResponseEntity<GenericResponse>
     */
    @GetMapping("/list/{userId}")
    public ResponseEntity<GenericResponse> getListFriendByUserId(@PathVariable("userId") String userId) {
        List<FriendResponse> friend = friendService.findFriendUserIdsByUserId(userId);
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get List Friend Successfully").result(friend).statusCode(HttpStatus.OK.value()).build());
    }


    //Laays danh saach ban be theo userId co su dung phan trang
    @GetMapping("/list/pageable/{userId}")
    public ResponseEntity<GenericResponse> getListFriendByUserId(@PathVariable("userId") String userId,
                                                                 //default page = 0, size = 10
                                                                 @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "5") int size) {
        List<FriendResponse> friend = friendService.findFriendUserIdsByUserIdPageable(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get List Friend Successfully").result(friend).statusCode(HttpStatus.OK.value()).build());
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<GenericResponse> deleteFriend(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @PathVariable("userId") String userId) {
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
    public ResponseEntity<GenericResponse> getSuggestionList(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String userIdToken = jwtService.extractUserId(token);
        return friendService.getFriendSuggestions(userIdToken);
    }

    // Lấy danh sách id của bạn bè theo userId
    @GetMapping("/list/friend-ids/{userId}")
    public List<String> getFriendIdsByUserId(@PathVariable String userId) {
        List<String> friendIds = friendRepository.findFriendIdsByAuthorId(userId);
        if (!friendIds.isEmpty()) {
            String jsonString = friendIds.get(0);
            JsonParser jsonParser = JsonParserFactory.getJsonParser();
            Map<String, Object> jsonMap = jsonParser.parseMap(jsonString);
            List<String> extractedFriendIds = (List<String>) jsonMap.get("fiend_ids");
            return extractedFriendIds;
        }
        return null;
    }

    @GetMapping("/status")
    public ResponseEntity<GenericResponse> getStatusByUserId(@RequestParam("userId") String userId, @RequestParam("userIdToken") String userIdToken) {
        Optional<Friend> friend = friendRepository.findByAuthorIdAndFriendIdsContaining(userId, userIdToken);
        Optional<Friend> friend2 = friendRepository.findByAuthorIdAndFriendIdsContaining(userIdToken, userId);

        if (friend.isPresent() || friend2.isPresent()) {
            return ResponseEntity.ok().body(new GenericResponse(true, "Bạn bè", null, HttpStatus.OK.value()));
        }

        Optional<FriendRequest> friendRequest = friendRequestRepository.findBySenderIdAndRecipientId(userId, userIdToken);
        if (friendRequest.isPresent()) {
            return ResponseEntity.ok().body(new GenericResponse(true, "Chấp nhận lời mời", null, HttpStatus.OK.value()));
        }

        Optional<FriendRequest> friendRequest1 = friendRequestRepository.findBySenderIdAndRecipientId(userIdToken, userId);
        if (friendRequest1.isPresent()) {
            return ResponseEntity.ok().body(new GenericResponse(true, "Đã gửi lời mời", null, HttpStatus.OK.value()));
        }

        return ResponseEntity.ok().body(new GenericResponse(true, "Kết bạn", null, HttpStatus.OK.value()));
    }

}
