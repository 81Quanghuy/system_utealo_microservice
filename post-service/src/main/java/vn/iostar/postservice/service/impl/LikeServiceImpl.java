package vn.iostar.postservice.service.impl;

import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.iostar.postservice.entity.Like;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.repository.LikeRepository;
import vn.iostar.postservice.repository.PostRepository;
import vn.iostar.postservice.service.LikeService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;




    @Override
    public Like createLike() {

        Post post = postRepository.findById("89eb66aa-0154-46fa-925d-e51059fd71fb").get();
        Like like = Like.builder()
                .id(UUID.randomUUID().toString())
                .userId("1")
                .post(post)
                .build();

        likeRepository.save(like);

        return like;
    }
}
