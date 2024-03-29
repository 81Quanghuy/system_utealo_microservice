package vn.iostar.postservice.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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

        UserProfileResponse userProfileResponse = userClientService.getUser(share.get().getUserId());

        if (share.isEmpty()) {
            throw new RuntimeException("Share Post not found.");
        } else if (currentUserId.equals(userProfileResponse.getUserId())) {
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
            @RequestParam(defaultValue = "20") Integer size) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        Pageable pageable = PageRequest.of(page, size);
        List<SharesResponse> sharePosts = shareService.findUserSharePosts(currentUserId, userId, pageable);

        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving share post successfully")
                .result(sharePosts).statusCode(HttpStatus.OK.value()).build());
    }

    // Tạo bài share
    @PostMapping("/create")
    public ResponseEntity<Object> createSharePost(@ModelAttribute SharePostRequestDTO requestDTO,
                                                  @RequestHeader("Authorization") String token) {
        return shareService.sharePost(token, requestDTO);
    }


    // Cập nhật bài share
    @PutMapping("/update")
    public ResponseEntity<Object> updateSharePost(@ModelAttribute SharePostRequestDTO requestDTO,
                                                  @RequestHeader("Authorization") String authorizationHeader,
                                                  BindingResult bindingResult) throws Exception {

        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return shareService.updateSharePost(requestDTO, currentUserId);
    }


    // Xóa bài share
    @DeleteMapping("/delete/{shareId}")
    public ResponseEntity<GenericResponse> deleteSharePost(@RequestHeader("Authorization") String token,
                                                           @PathVariable("shareId") String shareId, @RequestBody String userId) {
        return shareService.deleteSharePost(shareId, token, userId);
    }

    // Lấy những bài share của minh
    @GetMapping("/post")
    public ResponseEntity<GenericResponse> getMyShare(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        Pageable pageable = PageRequest.of(page, size);
        List<SharesResponse> sharePosts = shareService.findMySharePosts(currentUserId, pageable);

        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving share post successfully")
                .result(sharePosts).statusCode(HttpStatus.OK.value()).build());
    }

}
