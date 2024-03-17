package vn.iostar.postservice.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.postservice.dto.request.CreateCommentPostRequestDTO;
import vn.iostar.postservice.entity.Comment;
import vn.iostar.postservice.service.CommentService;

@RestController
@RequestMapping("/api/v1/post/comment")
public class CommentPostController {

    @Autowired
    CommentService commentService;

    // Tạo bình luận cho bài viết
    @PostMapping("/create")
    public ResponseEntity<Object> createCommentPost(@ModelAttribute CreateCommentPostRequestDTO requestDTO,
                                                    @RequestHeader("Authorization") String token) {
        return commentService.createCommentPost(token, requestDTO);
    }
}
