package vn.iostar.postservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.request.CreateCommentPostRequestDTO;
import vn.iostar.postservice.dto.response.CommentPostResponse;
import vn.iostar.postservice.dto.response.UserProfileResponse;
import vn.iostar.postservice.entity.Comment;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.jwt.service.JwtService;
import vn.iostar.postservice.repository.CommentRepository;
import vn.iostar.postservice.repository.PostRepository;
import vn.iostar.postservice.service.CloudinaryService;
import vn.iostar.postservice.service.CommentService;
import vn.iostar.postservice.service.PostService;
import vn.iostar.postservice.service.client.UserClientService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CloudinaryService cloudinaryService;
    private final UserClientService userClientService;
    private final JwtService jwtService;
    private final PostService postService;

    @Override
    public <S extends Comment> S save(S entity) {
        return commentRepository.save(entity);
    }
    @Override
    public ResponseEntity<Object> createCommentPost(String token, CreateCommentPostRequestDTO requestDTO) {
        String jwt = token.substring(7);
        String userId = jwtService.extractUserId(jwt);
        UserProfileResponse userProfileResponse = userClientService.getUser(userId);
        if (userProfileResponse == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        Optional<Post> post = postService.findById(requestDTO.getPostId());
        if (!post.isPresent()) {
            return ResponseEntity.badRequest().body("Post not found");
        }

        String commentId = UUID.randomUUID().toString();
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setPost(post.get());
        comment.setCreateTime(new Date());
        comment.setUpdatedAt(new Date());
        comment.setContent(requestDTO.getContent());
        try {
            if (requestDTO.getPhotos() == null || requestDTO.getPhotos().getContentType() == null) {
                comment.setPhotos("");
            } else {

                comment.setPhotos(cloudinaryService.uploadImage(requestDTO.getPhotos()));
            }
        } catch (IOException e) {
            // Xử lý ngoại lệ nếu có
            e.printStackTrace();
        }

        comment.setUserId(userProfileResponse.getUserId());
        save(comment);
        String commentIdOfPost = comment.getId();
        Post postOp = post.get();
        postOp.getCommentIds().add(comment);
        // Lưu post lại vào cơ sở dữ liệu
        postRepository.save(postOp);
        GenericResponse response = GenericResponse.builder().success(true).message("Comment Post Successfully")
                .result(new CommentPostResponse(comment.getId(), comment.getContent(), comment.getCreateTime(),
                        comment.getPhotos(), comment.getPost().getId(), userProfileResponse.getAvatar(),userProfileResponse.getUserName(), userProfileResponse.getUserId()))
                .statusCode(200).build();

        return ResponseEntity.ok(response);
    }

}
