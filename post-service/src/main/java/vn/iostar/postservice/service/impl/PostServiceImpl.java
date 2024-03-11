package vn.iostar.postservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import vn.iostar.postservice.constant.KafkaTopicName;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.request.CreatePostRequestDTO;
import vn.iostar.postservice.dto.response.PostsResponse;
import vn.iostar.postservice.dto.response.UserOfPostResponse;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.jwt.service.JwtService;
import vn.iostar.postservice.repository.PostRepository;
import vn.iostar.postservice.service.CloudinaryService;
import vn.iostar.postservice.service.PostService;


import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    private final JwtService jwtService;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MessageServiceImpl messageService;


    private CloudinaryService cloudinaryService;

    @Override
    public <S extends Post> S save(S entity) {
        return postRepository.save(entity);
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
        Post post = new Post();
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

        if (requestDTO.getPostGroupId() == null) {
            post.setGroupId(null);
        } else {
            post.setGroupId(requestDTO.getPostGroupId());
        }

        // Thiết lập các giá trị cố định
        post.setPostTime(new Date());
        post.setUpdatedAt(new Date());


        // Tiếp tục xử lý tạo bài đăng
        save(post);

        kafkaTemplate.send(KafkaTopicName.POST_TOPIC_GET_USER, userId);
        logger.info("Sent userId to Kafka");
        UserOfPostResponse userOfPostResponse = messageService.getLastReceivedUser();

        PostsResponse postsResponse = new PostsResponse(post, userOfPostResponse);
        List<Integer> count = new ArrayList<>();
        postsResponse.setComments(count);
        postsResponse.setLikes(count);


        GenericResponse response = GenericResponse.builder().success(true).message("Post Created Successfully")
                .result(postsResponse).statusCode(200).build();

        return ResponseEntity.ok(response);
    }
}
