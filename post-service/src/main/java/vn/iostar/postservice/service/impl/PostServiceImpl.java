package vn.iostar.postservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.iostar.postservice.constant.PrivacyLevel;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.request.CreatePostRequestDTO;
import vn.iostar.postservice.dto.request.PostUpdateRequest;
import vn.iostar.postservice.dto.response.GroupProfileResponse;
import vn.iostar.postservice.dto.response.PhoToResponse;
import vn.iostar.postservice.dto.response.PostsResponse;
import vn.iostar.postservice.dto.response.UserProfileResponse;
import vn.iostar.postservice.entity.Comment;
import vn.iostar.postservice.entity.Like;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.jwt.service.JwtService;
import vn.iostar.postservice.repository.PostRepository;
import vn.iostar.postservice.service.CloudinaryService;
import vn.iostar.postservice.service.PostService;
import vn.iostar.postservice.service.client.GroupClientService;
import vn.iostar.postservice.service.client.UserClientService;


import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final JwtService jwtService;

    private final UserClientService userClientService;

    private final GroupClientService groupClientService;

    private final CloudinaryService cloudinaryService;

    @Override
    public <S extends Post> S save(S entity) {
        return postRepository.save(entity);
    }

    @Override
    public Optional<Post> findById(String id) {
        return postRepository.findById(id);
    }

    @Override
    public ResponseEntity<Object> createUserPost(String token, CreatePostRequestDTO requestDTO) {

        List<String> allowedFileExtensions = Arrays.asList("docx", "txt", "pdf");

        if (requestDTO.getLocation() == null && requestDTO.getContent() == null) {
            return ResponseEntity.badRequest().body("Please provide all required fields.");
        }

        String accessToken = token.substring(7);
        String userId = jwtService.extractUserId(accessToken);

        // Tạo một đối tượng Post từ dữ liệu trong DTO
        String postId = UUID.randomUUID().toString();
        Post post = new Post();
        post.setId(postId);
        post.setLocation(requestDTO.getLocation());
        post.setContent(requestDTO.getContent());
        post.setPrivacyLevel(requestDTO.getPrivacyLevel());

        try {
            if (requestDTO.getPhotos() == null || requestDTO.getPhotos().getContentType() == null) {
                post.setPhotos("");
            } else {
                post.setPhotos(cloudinaryService.uploadImage(requestDTO.getPhotos()));
            }
            if (requestDTO.getFiles() == null || requestDTO.getFiles().getContentType() == null) {
                post.setFiles("");
            } else {
                String fileExtension = StringUtils.getFilenameExtension(requestDTO.getFiles().getOriginalFilename());
                if (fileExtension != null && allowedFileExtensions.contains(fileExtension.toLowerCase())) {
                    post.setFiles(cloudinaryService.uploadFile(requestDTO.getFiles()));
                } else {
                    throw new IllegalArgumentException("Not support for this file.");
                }
            }
        } catch (IOException e) {
            // Xử lý ngoại lệ nếu có
            e.printStackTrace();
        }

        if (userId == null) {
            return ResponseEntity.badRequest().body("User not found");
        } else {
            post.setUserId(userId);
        }

        GroupProfileResponse groupProfileResponse = null;
        if (requestDTO.getPostGroupId() == null) {
            post.setGroupId(null);
        } else {
            // Kiểm tra xem người dùng có quyền đăng bài trong nhóm không
            post.setGroupId(requestDTO.getPostGroupId());
            groupProfileResponse = groupClientService.getGroup(requestDTO.getPostGroupId());
        }

        // Thiết lập các giá trị cố định
        post.setPostTime(new Date());
        post.setUpdatedAt(new Date());
        // Tiếp tục xử lý tạo bài đăng
        save(post);

        // Lấy thông tin người dùng từ user-service
        UserProfileResponse userOfPostResponse = userClientService.getUser(userId);


        PostsResponse postsResponse = new PostsResponse(post, userOfPostResponse, groupProfileResponse);

        List<Integer> count = new ArrayList<>();
        postsResponse.setComments(count);
        postsResponse.setLikes(count);

        GenericResponse response = GenericResponse.builder().success(true).message("Post Created Successfully")
                .result(postsResponse).statusCode(200).build();

        return ResponseEntity.ok(response);
    }

    // Xóa bài post của mình
    @Override
    @Transactional
    public ResponseEntity<GenericResponse> deletePost(String postId, String token, String userId) {

        String accessToken = token.substring(7);
        String currentUserId = jwtService.extractUserId(accessToken);
        String a = userId.replace("\"", "").replace("\r\n\r\n", "");;
        if (!currentUserId.equals(userId.replace("\"", "").replace("\r\n\r\n", ""))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse(false, "Delete denied!", null, HttpStatus.NOT_FOUND.value()));
        }
        Optional<Post> optionalPost = findById(postId);
        Optional<Post> optionalPost1 = findById("65f6c6d08295e9313b7aee1c");
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            postRepository.delete(post);

            return ResponseEntity.ok()
                    .body(new GenericResponse(true, "Delete Successful!", null, HttpStatus.OK.value()));
        }
        // Khi không tìm thấy bài post với id
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse(false, "Cannot found post!", null, HttpStatus.NOT_FOUND.value()));
        }
    }

    @Override
    public ResponseEntity<Object> updatePost(String postId, PostUpdateRequest request, String currentUserId)
            throws Exception {

        List<String> allowedFileExtensions = Arrays.asList("docx", "txt", "pdf");
        UserProfileResponse userOfPostResponse = userClientService.getUser(currentUserId);
        GroupProfileResponse groupProfileResponse = null;


        Optional<Post> postOp = findById(postId);
        if (postOp.isEmpty()) {
            throw new Exception("Post doesn't exist");
        }
        Post post = postOp.get();
        if (!currentUserId.equals(postOp.get().getUserId())) {
            throw new Exception("Update denied");
        }
        post.setContent(request.getContent());
        post.setLocation(request.getLocation());
        post.setPrivacyLevel(request.getPrivacyLevel());
        post.setUpdatedAt(new Date());
        if(post.getGroupId() != null) {
            groupProfileResponse = groupClientService.getGroup(post.getGroupId());
        }
        try {
            if (request.getPhotos() == null || request.getPhotos().getContentType() == null) {
                post.setPhotos(request.getPhotoUrl());
            } else {
                post.setPhotos(cloudinaryService.uploadImage(request.getPhotos()));
            }

            if (request.getFiles() == null || request.getFiles().getContentType() == null) {
                post.setFiles(request.getFileUrl());
            } else {
                String fileExtension = StringUtils.getFilenameExtension(request.getFiles().getOriginalFilename());
                if (fileExtension != null && allowedFileExtensions.contains(fileExtension.toLowerCase())) {
                    post.setFiles(cloudinaryService.uploadFile(request.getFiles()));
                } else {
                    throw new IllegalArgumentException("Not support for this file.");
                }
            }
        } catch (IOException e) {
            // Xử lý ngoại lệ nếu có
            e.printStackTrace();
        }
        save(post);
        PostsResponse postResponse = new PostsResponse(post, userOfPostResponse, groupProfileResponse);
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Update successful")
                .result(postResponse).statusCode(200).build());
    }

    @Override
    public ResponseEntity<GenericResponse> getPost(String currentId, String postId) {
        Optional<Post> post = postRepository.findById(postId);
        UserProfileResponse userOfPostResponse = userClientService.getUser(currentId);
        GroupProfileResponse groupProfileResponse = null;
        if (post.get().getGroupId() != null) {
            groupProfileResponse = groupClientService.getGroup(post.get().getGroupId());
        }
        if (post.isEmpty())
            return ResponseEntity.ok(GenericResponse.builder().success(null).message("not found post").result(null)
                    .statusCode(HttpStatus.NOT_FOUND.value()).build());

        PostsResponse postsResponse = new PostsResponse(post.get(), userOfPostResponse, groupProfileResponse);
        postsResponse.setComments(getIdComment(post.get().getCommentIds()));
        postsResponse.setLikes(getIdLikes(post.get().getLikeIds()));

        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving user profile successfully")
                .result(postsResponse).statusCode(HttpStatus.OK.value()).build());
    }

    private List<Integer> getIdLikes(List<Like> likes) {
        if (likes == null) {
            return Collections.emptyList();
        }
        List<Integer> idLikes = new ArrayList<>();
        for (Like like : likes) {
            idLikes.add(Integer.valueOf(like.getId()));
        }
        return idLikes;
    }

    private List<Integer> getIdComment(List<Comment> comments) {
        if (comments == null) {
            return Collections.emptyList();
        }
        List<Integer> idComments = new ArrayList<>();
        for (Comment cmt : comments) {
            idComments.add(Integer.valueOf(cmt.getId()));
        }
        return idComments;
    }


    @Override
    public List<PostsResponse> findUserPosts(String currentUserId, String userId, Pageable pageable) {
        List<PrivacyLevel> privacyLevels = Arrays.asList(PrivacyLevel.PUBLIC, PrivacyLevel.FRIENDS);
        if (currentUserId.equals(userId))
            privacyLevels = Arrays.asList(PrivacyLevel.PUBLIC, PrivacyLevel.FRIENDS, PrivacyLevel.PRIVATE);

        List<Post> userPosts = postRepository.findByUserIdAndPrivacyLevelInOrderByPostTimeDesc(userId,
                privacyLevels, pageable);

        UserProfileResponse userOfPostResponse = userClientService.getUser(userId);


        List<PostsResponse> simplifiedUserPosts = new ArrayList<>();
        for (Post post : userPosts) {

            PostsResponse postsResponse = new PostsResponse(post, userOfPostResponse, null);
            postsResponse.setComments(getIdComment(post.getCommentIds()));
            postsResponse.setLikes(getIdLikes(post.getLikeIds()));
            simplifiedUserPosts.add(postsResponse);
        }
        return simplifiedUserPosts;
    }

    @Override
    public List<String> findAllPhotosByUserIdOrderByPostTimeDesc(String userId) {
        List<String> jsonStrings = postRepository.findAllPhotosByUserIdOrderByPostTimeDesc(userId);
        List<String> photos = new ArrayList<>();

        for (String jsonString : jsonStrings) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject.has("photos")) {
                    photos.add(jsonObject.getString("photos"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return photos;
    }


    @Override
    public ResponseEntity<Object> findLatestPhotosByUserId(String currentUserId, String userId, Pageable pageable) {
        UserProfileResponse userOfPostResponse = userClientService.getUser(userId);
        if (userOfPostResponse == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
                    .message("User not found.").statusCode(HttpStatus.NOT_FOUND.value()).build());
        List<PrivacyLevel> privacyLevels = Arrays.asList(PrivacyLevel.GROUP_MEMBERS);
        if (!currentUserId.equals(userId)) {
            privacyLevels = Arrays.asList(PrivacyLevel.GROUP_MEMBERS, PrivacyLevel.PRIVATE);
        }
        List<PhoToResponse> list = postRepository.findLatestPhotosByUserIdAndNotNull(privacyLevels, userId, pageable);
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieved user posts successfully")
                .result(list).statusCode(HttpStatus.OK.value()).build());

    }

}
