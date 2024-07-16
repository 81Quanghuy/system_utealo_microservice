package vn.iostar.postservice.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.groupservice.dto.FilesOfGroupDTO;
import vn.iostar.groupservice.dto.PhotosOfGroupDTO;
import vn.iostar.model.DateResponse;
import vn.iostar.model.PostElastic;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.request.CreatePostRequestDTO;
import vn.iostar.postservice.dto.request.PostUpdateRequest;
import vn.iostar.postservice.dto.response.GroupProfileResponse;
import vn.iostar.postservice.dto.response.PostsResponse;
import vn.iostar.postservice.dto.response.UserProfileResponse;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.jwt.service.JwtService;
import vn.iostar.postservice.repository.jpa.PostRepository;
import vn.iostar.postservice.service.PostService;
import vn.iostar.postservice.service.ShareService;
import vn.iostar.postservice.service.client.GroupClientService;
import vn.iostar.postservice.service.client.UserClientService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController {

    private final PostService postService;
    private final JwtService jwtService;
    private final ShareService shareService;
    private final GroupClientService groupClientService;
    private final PostRepository postRepository;
    private final UserClientService userClientService;

    // Xem chi tiết bài viết
    @GetMapping("/{postId}")
    public ResponseEntity<GenericResponse> getPost(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("postId") String postId) throws JsonProcessingException {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return postService.getPost(currentUserId, postId);
    }

    // Lấy bài viết theo userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<GenericResponse> getPostByUserId(@RequestHeader("Authorization") String authorizationHeader,
                                                           @PathVariable("userId") String userId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "20") int size)
            throws JsonProcessingException {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        Pageable pageable = PageRequest.of(page, size);
        List<PostsResponse> userPosts = postService.findUserPosts(currentUserId, userId, pageable);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Retrieved user posts successfully and access update")
                .result(userPosts)
                .statusCode(HttpStatus.OK.value()).build());
    }

    // Tạo bài viết
    @PostMapping("/create")
    public ResponseEntity<Object> createUserPost(@ModelAttribute CreatePostRequestDTO requestDTO,
                                                 @RequestHeader(HttpHeaders.AUTHORIZATION) String token)
            throws JsonProcessingException {
        return postService.createUserPost(token, requestDTO);
    }

    // Xóa bài viết
    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<GenericResponse> deletePost(@RequestHeader("Authorization") String token,
                                                      @PathVariable("postId") String postId,
                                                      @RequestBody String userId) {
        return postService.deletePost(postId, token, userId);

    }

    // Sửa bài viết
    @PutMapping("/update/{postId}")
    public ResponseEntity<Object> updatePost(@ModelAttribute PostUpdateRequest request,
                                             @RequestHeader("Authorization") String authorizationHeader,
                                             @PathVariable("postId") String postId)
            throws Exception {
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
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "9") int size,
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
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) throws JsonProcessingException {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return postService.getPostTimelineByUserId(currentUserId, page, size);
    }

    // Lấy tất cả bài post của 1 nhóm
    @GetMapping("/{postGroupId}/posts")
    public ResponseEntity<GenericResponse> getPostOfPostGroup(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable String postGroupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) throws JsonProcessingException {
        String token = authorizationHeader.substring(7);
        String userIdToken = jwtService.extractUserId(token);
        return postService.getGroupPosts(userIdToken, postGroupId, page, size);
    }

    // Lấy tất cả các bài post của những nhóm mình tham gia
    @GetMapping("/inGroup")
    public ResponseEntity<GenericResponse> getShareOfUserPostGroup(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) throws JsonProcessingException {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        return postService.getPostOfPostGroup(currentUserId, page, size);
    }

    // Lấy danh sách file của 1 nhóm
    @GetMapping("/files/{groupId}")
    public List<FilesOfGroupDTO> getLatestFilesOfGroup(@PathVariable("groupId") String groupId) {
        return postService.findLatestFilesByGroupId(groupId);
    }

    // Lấy danh sách photo của 1 nhóm
    @GetMapping("/photos/{groupId}")
    public Page<Object> getLatestPhotoOfGroup(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "5") int size,
                                                        @PathVariable("groupId") String groupId) {
        return postService.findLatestPhotosByGroupId(groupId, page, size);
    }

    // Lấy những bài viết trong nhóm do Admin đăng
    @GetMapping("/roleAdmin/{groupId}")
    public ResponseEntity<GenericResponse> getPostsByAdminRoleInGroup(@PathVariable("groupId") String groupId,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<PostsResponse> groupPosts = postService.findPostsByAdminRoleInGroup(groupId, pageable);
        if (groupPosts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false).message("No posts found for admin of this group").statusCode(HttpStatus.NOT_FOUND.value()).build());
        } else {
            return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieved posts of admin successfully").result(groupPosts).statusCode(HttpStatus.OK.value()).build());
        }
    }

    @GetMapping("/searchPost")
    public List<PostsResponse> getPosts(@RequestParam("search") String search) {
        Pageable pageable = PageRequest.of(0, 3);
        List<Post> posts = postRepository.findByContentIgnoreCaseContaining(search, pageable);
        List<PostsResponse> simplifiedUserPosts = new ArrayList<>();
        GroupProfileResponse groupProfileResponse = null;
        for (Post post : posts) {
            UserProfileResponse user = userClientService.getUser(post.getUserId());
            if (post.getGroupId() != null && !post.getGroupId().isEmpty()) {
                groupProfileResponse = groupClientService.getGroup(post.getGroupId());
            }
            PostsResponse postsResponse = new PostsResponse(post, user, groupProfileResponse);
            simplifiedUserPosts.add(postsResponse);
        }
        return simplifiedUserPosts;
    }

    // search post by content and location
    @GetMapping("/search")
    public List<PostElastic> searchPost(@RequestParam("search") String search,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size)
            throws IOException {
        Pageable pageable = PageRequest.of(page, size);
        return postService.searchPost(search, pageable);
    }

    // get so luong bai post cua user
    @PostMapping("/countPosts/{userId}")
    public Long countPostsByUserId(@PathVariable String userId, @RequestBody DateResponse dateResponse) {
        return postService.countPostsByUserId(userId, dateResponse.getStartDate(), dateResponse.getEndDate());
    }
}
