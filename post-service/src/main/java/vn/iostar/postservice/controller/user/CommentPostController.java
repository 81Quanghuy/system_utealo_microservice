package vn.iostar.postservice.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.request.CommentUpdateRequest;
import vn.iostar.postservice.dto.request.CreateCommentPostRequestDTO;
import vn.iostar.postservice.dto.request.ReplyCommentPostRequestDTO;
import vn.iostar.postservice.entity.Comment;
import vn.iostar.postservice.jwt.service.JwtService;
import vn.iostar.postservice.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post/comment")
public class CommentPostController {

    private final CommentService commentService;
    private final JwtService jwtService;

    // Lấy danh sách bình luận của 1 bài viết
    @GetMapping("/{postId}")
    public ResponseEntity<GenericResponse> getCommentOfPost(@PathVariable("postId") String postId) {
        return commentService.getCommentOfPost(postId);
    }

    // Tạo bình luận cho bài viết
    @PostMapping("/create")
    public ResponseEntity<Object> createCommentPost(@ModelAttribute CreateCommentPostRequestDTO requestDTO,
                                                    @RequestHeader("Authorization") String token) {
        return commentService.createCommentPost(token, requestDTO);
    }

    // Phản hồi bình luận
    @PostMapping("/reply")
    public ResponseEntity<Object> replyCommentPost(@ModelAttribute ReplyCommentPostRequestDTO requestDTO,
                                                   @RequestHeader("Authorization") String token) {
        return commentService.replyCommentPost(token, requestDTO);
    }

    // Cập nhật bình luận
    @PutMapping("/update/{commentId}")
    public ResponseEntity<Object> updateUser(@ModelAttribute CommentUpdateRequest request,
                                             @RequestHeader("Authorization") String authorizationHeader, @PathVariable("commentId") String commentId,
                                             BindingResult bindingResult) throws Exception {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return commentService.updateComment(commentId, request,currentUserId);

    }

    // Xóa bình luận
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<GenericResponse> deleteCommentOfPost(@RequestHeader("Authorization") String authorizationHeader,
                                                               @PathVariable("commentId") String commentId) {
        return commentService.deleteCommentOfPost(commentId);
    }

    // Lấy danh sách phản hổi của 1 bình luận
    @GetMapping("/{commentId}/commentReply")
    public ResponseEntity<GenericResponse> getCommentReplyOfComment(@PathVariable("commentId") String commentId) {
        return commentService.getCommentReplyOfComment(commentId);
    }

    // Lấy số lượng bình luận của 1 bài viết
    @GetMapping("/number/{postId}")
    public ResponseEntity<GenericResponse> getCountCommentOfPost(
            @PathVariable("postId") String postId) {
        return commentService.getCountCommentOfPost(postId);
    }
}
