package vn.iostar.postservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.iostar.postservice.constant.PrivacyLevel;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.entity.Share;
import vn.iostar.postservice.repository.PostRepository;
import vn.iostar.postservice.repository.ShareRepository;
import vn.iostar.postservice.service.ShareService;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {

    private final ShareRepository shareRepository;
    private final PostRepository postRepository;
    @Override
    public Share createShare() {

        Post post = postRepository.findById("89eb66aa-0154-46fa-925d-e51059fd71fb").get();
        Share share = Share.builder()
                .id(UUID.randomUUID().toString())
                .post(post)
                .content("Good")
                .privacyLevel(PrivacyLevel.PUBLIC)
                .userId("1")
                .createAt(new Date())
                .updateAt(new Date())
                .build();

        shareRepository.save(share);
        return share;
    }
}
