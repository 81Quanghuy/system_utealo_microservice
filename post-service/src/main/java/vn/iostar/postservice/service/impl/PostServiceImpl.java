package vn.iostar.postservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.iostar.postservice.constant.PrivacyLevel;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.repository.PostRepository;
import vn.iostar.postservice.service.PostService;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    @Override
    public Post createPost() {

        Post ost = Post.builder()
                .id(UUID.randomUUID().toString())
                .content("Hello")
                .files("file")
                .photos("photo")
                .location("location")
                .privacyLevel(PrivacyLevel.PUBLIC)
                .userId("1")
                .groupId("1")
                .postTime(new Date())
                .updatedAt(new Date())
                .build();
        postRepository.save(ost);

        return ost;

    }
}
