package vn.iostar.postservice.service;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.request.CreatePostRequestDTO;
import vn.iostar.postservice.dto.request.PostUpdateRequest;
import vn.iostar.postservice.dto.response.PostsResponse;
import vn.iostar.postservice.entity.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {

    <S extends Post> S save(S entity);
    Optional<Post> findById(String id);
    // Tạo bài post
    ResponseEntity<Object> createUserPost(String token, CreatePostRequestDTO requestDTO);
    // Xóa bài post của mình
    ResponseEntity<GenericResponse> deletePost(String postId, String token, String userId);
    // Sửa bài post của mình
    ResponseEntity<Object> updatePost(String postId, PostUpdateRequest request, String currentUserId) throws Exception;
    // Xem chi tiết bài post
    ResponseEntity<GenericResponse> getPost(String userIdToken, String postId);
    // Lấy những bài post của mình
    List<PostsResponse> findUserPosts(String currentUserId, String userId, Pageable pageable);
    // Lấy tất cả hình của user đó
    List<String> findAllPhotosByUserIdOrderByPostTimeDesc(String userId);
    // Lấy 9 hình đầu tiên của user
    ResponseEntity<Object> findLatestPhotosByUserId(String currentUserId, String userId, Pageable pageable);

}
