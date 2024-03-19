package vn.iostar.postservice.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.service.LikeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post/like")
public class LikePostController {

    private final LikeService likeService;

    // Lấy danh sách like của bài post
    @GetMapping("/{postId}")
    public ResponseEntity<GenericResponse> getLikeOfPost(
            @PathVariable("postId") String postId) {
        return likeService.getLikeOfPost(postId);
    }

    // Lấy số lượng like của bài post
    @GetMapping("/number/{postId}")
    public ResponseEntity<GenericResponse> getNumberLikeOfPost(
            @PathVariable("postId") String postId) {
        return likeService.getCountLikeOfPost(postId);
    }

    // Like hoặc unlike bài post
    // Unlike chua duoc
    @PostMapping("/toggleLike/{postId}")
    public ResponseEntity<Object> toggleLikePost(@PathVariable("postId") String postId,
                                                 @RequestHeader("Authorization") String token) {
        return likeService.toggleLikePost(token, postId);
    }

    // Kiểm tra xem user đã like bài post chưa
    @GetMapping("/checkUser/{postId}")
    public ResponseEntity<Object> checkUserLikePost(@PathVariable("postId") String postId,
                                                    @RequestHeader("Authorization") String token) {
        return likeService.checkUserLikePost(token, postId);
    }

    // Lấy danh sách những người đã like bài post
    @GetMapping("/listUser/{postId}")
    public ResponseEntity<Object> listUserLikePost(@PathVariable("postId") String postId) {
        return likeService.listUserLikePost(postId);
    }
}
