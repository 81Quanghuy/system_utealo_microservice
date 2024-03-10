package vn.iostar.postservice.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.iostar.postservice.entity.Like;
import vn.iostar.postservice.service.LikeService;

@RestController
@RequestMapping("/api/v1/like")
public class LikeController {

    @Autowired
    LikeService likeService;

    @PostMapping("/create")
    public ResponseEntity<Like> createLike() {
        return ResponseEntity.ok(likeService.createLike());
    }
}
