package vn.iostar.postservice.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.SharePostRequestDTO;
import vn.iostar.postservice.dto.response.SharesResponse;
import vn.iostar.postservice.dto.response.UserProfileResponse;
import vn.iostar.postservice.entity.Share;
import vn.iostar.postservice.jwt.service.JwtService;
import vn.iostar.postservice.service.ShareService;
import vn.iostar.postservice.service.client.UserClientService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/share")
public class ShareController {

    private final ShareService shareService;
    private final JwtService jwtService;
    private final UserClientService userClientService;

    // Lấy chi tiết bài share theo shareId
    @GetMapping("/{shareId}")
    public ResponseEntity<GenericResponse> getShareByShareId(@RequestHeader("Authorization") String authorizationHeader,
                                                             @PathVariable("shareId") String shareId) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        Optional<Share> share = shareService.findById(shareId);
        if (share.isEmpty())
            throw new RuntimeException("Share Post not found.");
        UserProfileResponse userProfileResponse = userClientService.getUser(share.get().getUserId());

        if (currentUserId.equals(userProfileResponse.getUserId())) {
            SharesResponse sharePosts = shareService.getSharePost(share.get(), currentUserId);
            return ResponseEntity.ok(
                    GenericResponse.builder().success(true).message("Retrieving share post successfully and access update")
                            .result(sharePosts).statusCode(HttpStatus.OK.value()).build());
        } else {
            return ResponseEntity.ok(GenericResponse.builder().success(true)
                    .message("Retrieving share post successfully and access update denied")
                    .statusCode(HttpStatus.OK.value()).build());
        }
    }

    // Lấy những bài share của user theo UserId
    @GetMapping("/{userId}/post")
    public ResponseEntity<GenericResponse> getShareByUserId(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("userId") String userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size)  {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        Pageable pageable = PageRequest.of(page, size);

        List<SharesResponse> sharePosts = shareService.findUserSharePosts(currentUserId, userId, pageable);
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving share post successfully")
                .result(sharePosts).statusCode(HttpStatus.OK.value()).build());
    }

    // Tạo bài share
    @PostMapping("/create")
    public ResponseEntity<Object> createSharePost(@RequestBody SharePostRequestDTO requestDTO,
                                                  @RequestHeader("Authorization") String token) {
        return shareService.sharePost(token, requestDTO);
    }


    // Cập nhật bài share
    @PutMapping("/update")
    public ResponseEntity<Object> updateSharePost(@ModelAttribute SharePostRequestDTO requestDTO,
                                                  @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return shareService.updateSharePost(requestDTO, currentUserId);
    }

    // Xóa bài share
    @DeleteMapping("/delete/{shareId}")
    public ResponseEntity<GenericResponse> deleteSharePost(@RequestHeader("Authorization") String token,
                                                           @PathVariable("shareId") String shareId,
                                                           @RequestBody String userId) {
        return shareService.deleteSharePost(shareId, token, userId);
    }

    // Lấy những bài share liên quan đến mình như: nhóm, bạn bè, cá nhân
    @GetMapping("/get/timeLine")
    public ResponseEntity<GenericResponse> getShareTimeLine(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) throws JsonProcessingException {

        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return shareService.getTimeLineSharePosts(currentUserId, page, size);
    }

    // Lấy tất cả các bài share của những nhóm mình tham gia
    @GetMapping("/inGroup")
    public ResponseEntity<GenericResponse> getShareOfUserPostGroup(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) throws JsonProcessingException {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return shareService.getShareOfPostGroup(currentUserId, page, size);
    }

    // Lấy những bài share post của nhóm
    @GetMapping("/{postGroupId}/shares")
    public ResponseEntity<GenericResponse> getGroupSharePosts(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("postGroupId") String postGroupId, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) throws JsonProcessingException {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return shareService.getGroupSharePosts(currentUserId, postGroupId, page, size);
    }
}
