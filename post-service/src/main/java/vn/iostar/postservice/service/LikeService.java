package vn.iostar.postservice.service;

import org.springframework.http.ResponseEntity;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.response.UserProfileResponse;
import vn.iostar.postservice.entity.Comment;
import vn.iostar.postservice.entity.Like;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.entity.Share;

import java.util.Optional;

public interface LikeService {
    void delete(Like entity);
    <S extends Like> S save(S entity);
    Optional<Like> findById(String id);
    ResponseEntity<GenericResponse> getLikeOfPost(String postId);
    ResponseEntity<GenericResponse> getCountLikeOfPost(String postId);
    ResponseEntity<Object> toggleLikePost(String token,String postId );
    Optional<Like> findByPostAndUser(Post post, UserProfileResponse user);
    ResponseEntity<Object> checkUserLikePost(String token,String postId );
    ResponseEntity<Object> listUserLikePost(String postId );
    ResponseEntity<GenericResponse> getLikeOfShare(String shareId);
    ResponseEntity<Object> toggleLikeShare(String token,String shareId );
    Optional<Like> findByShareAndUser(Share share, UserProfileResponse user);
    ResponseEntity<Object> checkUserLikeShare(String token,String shareId );
    ResponseEntity<Object> listUserLikeShare(String shareId );
    ResponseEntity<GenericResponse> getLikeOfComment(String commentId);
    ResponseEntity<GenericResponse> getCountLikeOfComment(String commentId);
    ResponseEntity<Object> toggleLikeComment(String token,String commentId );
    Optional<Like> findByCommentAndUser(Comment comment, UserProfileResponse user);
    ResponseEntity<Object> checkUserLikeComment(String token,String commentId );
    ResponseEntity<Object> listUserLikeComment(String commentId );
}
