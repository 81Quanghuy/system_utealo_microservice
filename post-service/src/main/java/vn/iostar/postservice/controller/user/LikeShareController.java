package vn.iostar.postservice.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.entity.Like;
import vn.iostar.postservice.jwt.service.JwtService;
import vn.iostar.postservice.repository.ShareRepository;
import vn.iostar.postservice.service.LikeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/share/like")
public class LikeShareController {

    private final JwtService jwtService;
    private final LikeService likeService;
    private final ShareRepository shareRepository;

    @GetMapping("/{shareId}")
    public ResponseEntity<GenericResponse> getLikeOfShare(@PathVariable("shareId") String shareId) {
        return likeService.getLikeOfShare(shareId);
    }

    @PostMapping("/toggleLike/{shareId}")
    public ResponseEntity<Object> toggleLikeShare(@PathVariable("shareId") String shareId,
                                                  @RequestHeader("Authorization") String token) {
        return likeService.toggleLikeShare(token, shareId);
    }

    @GetMapping("/checkUser/{shareId}")
    public ResponseEntity<Object> checkUserLikePost(@PathVariable("shareId") String shareId,
                                                    @RequestHeader("Authorization") String token) {
        return likeService.checkUserLikeShare(token, shareId);
    }

    // Lấy danh sách những người đã like comment
    @GetMapping("/listUser/{shareId}")
    public ResponseEntity<Object> listUserLikeShare(@PathVariable("shareId") String shareId) {
        return likeService.listUserLikeShare(shareId);
    }

}
