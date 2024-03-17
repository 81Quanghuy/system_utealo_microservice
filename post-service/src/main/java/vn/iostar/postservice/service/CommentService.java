package vn.iostar.postservice.service;

import org.springframework.http.ResponseEntity;
import vn.iostar.postservice.dto.request.CreateCommentPostRequestDTO;
import vn.iostar.postservice.entity.Comment;

public interface CommentService {
    <S extends Comment> S save(S entity);
    ResponseEntity<Object> createCommentPost(String token, CreateCommentPostRequestDTO requestDTO);
}
