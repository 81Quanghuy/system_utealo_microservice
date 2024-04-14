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
import vn.iostar.groupservice.dto.FilesOfGroupDTO;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.request.CreatePostRequestDTO;
import vn.iostar.postservice.dto.request.PostUpdateRequest;
import vn.iostar.postservice.dto.response.PostsResponse;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.jwt.service.JwtService;
import vn.iostar.postservice.service.PostService;
import vn.iostar.postservice.service.ShareService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController {

    private final PostService postService;
    private final JwtService jwtService;
    private final ShareService shareService;

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

    // Xem chi tiết bài share
    @GetMapping("/share/{shareId}")
    public ResponseEntity<GenericResponse> getShare(@RequestHeader("Authorization") String authorizationHeader,
                                                    @PathVariable("shareId") String shareId) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return shareService.getShare(currentUserId, shareId);
    }

    // Lấy những bài post liên quan đến mình như: nhóm, bạn bè, cá nhân
    @GetMapping("/get/timeLine")
    public ResponseEntity<GenericResponse> getPostsByUserAndFriendsAndGroups(
            @RequestHeader("Authorization") String authorizationHeader, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return postService.getPostTimelineByUserId(currentUserId, page, size);
    }

    // Lấy tất cả bài post của 1 nhóm
    @GetMapping("/{postGroupId}/posts")
    public ResponseEntity<GenericResponse> getPostOfPostGroup(
            @RequestHeader("Authorization") String authorizationHeader, @PathVariable String postGroupId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        String token = authorizationHeader.substring(7);
        String userIdToken = jwtService.extractUserId(token);
        return postService.getGroupPosts(userIdToken, postGroupId, page, size);
    }

    // Lấy tất cả các bài post của những nhóm mình tham gia
    @GetMapping("/inGroup")
    public ResponseEntity<GenericResponse> getShareOfUserPostGroup(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        Pageable pageable = PageRequest.of(page, size);
        return postService.getPostOfPostGroup(currentUserId, pageable);
    }

    // Lấy danh sách file của 1 nhóm
    @GetMapping("/files/{groupId}")
    public List<FilesOfGroupDTO> getLatestFilesOfGroup(@PathVariable("groupId") String groupId) {
        return postService.findLatestFilesByGroupId(groupId);
    }
}
