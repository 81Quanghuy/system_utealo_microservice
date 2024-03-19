package vn.iostar.postservice.service;

import org.springframework.http.ResponseEntity;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.response.UserProfileResponse;
import vn.iostar.postservice.entity.Like;
import vn.iostar.postservice.entity.Post;

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
}
