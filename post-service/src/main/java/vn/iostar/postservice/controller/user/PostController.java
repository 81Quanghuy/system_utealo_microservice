package vn.iostar.postservice.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.request.CreatePostRequestDTO;
import vn.iostar.postservice.dto.request.PostUpdateRequest;
import vn.iostar.postservice.dto.response.PostsResponse;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.jwt.service.JwtService;
import vn.iostar.postservice.service.PostService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController {

    private final PostService postService;
    private final JwtService jwtService;

    // Xem chi tiết bài viết
    @GetMapping("/{postId}")
    public ResponseEntity<GenericResponse> getPost(@RequestHeader("Authorization") String authorizationHeader,
                                                   @PathVariable("postId") String postId) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return postService.getPost(currentUserId, postId);
    }

    // Lấy bài viết theo userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<GenericResponse> getPostByUserId(@RequestHeader("Authorization") String authorizationHeader,
                                                           @PathVariable("userId") String userId, @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "20") int size) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        Pageable pageable = PageRequest.of(page, size);
        List<PostsResponse> userPosts = postService.findUserPosts(currentUserId, userId, pageable);

        return ResponseEntity.ok(
                GenericResponse.builder().success(true).message("Retrieved user posts successfully and access update")
                        .result(userPosts).statusCode(HttpStatus.OK.value()).build());
    }

    // Tạo bài viết
    @PostMapping("/create")
    public ResponseEntity<Object> createUserPost(@ModelAttribute CreatePostRequestDTO requestDTO,
                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return postService.createUserPost(token, requestDTO);
    }

    // Xóa bài viết
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<GenericResponse> deletePost(@RequestHeader("Authorization") String token,
                                                      @PathVariable("postId") String postId, @RequestBody String userId) {
        return postService.deletePost(postId, token, userId);

    }

    // Sửa bài viết
    @PutMapping("/update/{postId}")
    public ResponseEntity<Object> updatePost(@ModelAttribute PostUpdateRequest request,
                                             @RequestHeader("Authorization") String authorizationHeader, @PathVariable("postId") String postId,
                                             BindingResult bindingResult) throws Exception {

        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return postService.updatePost(postId, request, currentUserId);

    }


    // Lấy bài viết
    @GetMapping("/getPost/{postId}")
    public Post getGroup(@PathVariable String postId) {
        Optional<Post> post = postService.findById(postId);

        if (post.isEmpty()) {
            throw new RuntimeException("Post not found.");
        }
        return post.get();

    }

    // Lấy tất cả hình của user đó
    @GetMapping("/user/{userId}/photos")
    public List<String> findAllPhotosByUserIdOrderByPostTimeDesc(@PathVariable String userId) {
        List<String> photos = postService.findAllPhotosByUserIdOrderByPostTimeDesc(userId);
        return postService.findAllPhotosByUserIdOrderByPostTimeDesc(userId);
    }

    // Lấy 9 hình đầu tiên của user
    @GetMapping("/getPhotos/{userId}")
    public ResponseEntity<Object> getLatestPhotosByUserId(@RequestHeader("Authorization") String authorizationHeader,
                                                          @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "9") int size,
                                                          @PathVariable("userId") String userId) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        Pageable pageable = PageRequest.of(page, size);
        return postService.findLatestPhotosByUserId(currentUserId, userId, pageable);
    }



}
