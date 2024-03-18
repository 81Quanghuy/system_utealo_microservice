package vn.iostar.postservice.service;

import org.springframework.http.ResponseEntity;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.request.CommentUpdateRequest;
import vn.iostar.postservice.dto.request.CreateCommentPostRequestDTO;
import vn.iostar.postservice.dto.request.ReplyCommentPostRequestDTO;
import vn.iostar.postservice.entity.Comment;

import java.util.Optional;

public interface CommentService {
    <S extends Comment> S save(S entity);
    Optional<Comment> findById(String id);
    ResponseEntity<GenericResponse> getCommentOfPost(String postId);
    ResponseEntity<Object> createCommentPost(String token, CreateCommentPostRequestDTO requestDTO);
    ResponseEntity<Object> replyCommentPost(String token, ReplyCommentPostRequestDTO requestDTO);
    ResponseEntity<Object> updateComment(String commentId, CommentUpdateRequest request, String currentUserId)
            throws Exception;
    ResponseEntity<GenericResponse> deleteCommentOfPost(String commentId);
    ResponseEntity<GenericResponse> getCommentReplyOfComment(String commentId);
    ResponseEntity<GenericResponse> getCountCommentOfPost(String postId);
}
