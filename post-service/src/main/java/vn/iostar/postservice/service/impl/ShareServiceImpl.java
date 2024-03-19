package vn.iostar.postservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iostar.postservice.constant.PrivacyLevel;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.SharePostRequestDTO;
import vn.iostar.postservice.dto.response.GroupProfileResponse;
import vn.iostar.postservice.dto.response.SharesResponse;
import vn.iostar.postservice.dto.response.UserProfileResponse;
import vn.iostar.postservice.entity.Post;
import vn.iostar.postservice.entity.Share;
import vn.iostar.postservice.jwt.service.JwtService;
import vn.iostar.postservice.repository.PostRepository;
import vn.iostar.postservice.repository.ShareRepository;
import vn.iostar.postservice.service.PostService;
import vn.iostar.postservice.service.ShareService;
import vn.iostar.postservice.service.client.GroupClientService;
import vn.iostar.postservice.service.client.UserClientService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {

    private final ShareRepository shareRepository;
    private final PostRepository postRepository;
    private final PostService postService;
    private final JwtService jwtService;
    private final UserClientService userClientService;
    private final GroupClientService groupClientService;

    @Override
    public <S extends Share> S save(S entity) {
        return shareRepository.save(entity);
    }

    @Override
    public Optional<Share> findById(String id) {
        return shareRepository.findById(id);
    }

    @Override
    public ResponseEntity<Object> sharePost(String token, SharePostRequestDTO requestDTO) {
        String jwt = token.substring(7);
        String userId = jwtService.extractUserId(jwt);
        UserProfileResponse user = userClientService.getUser(userId);
        if (user == null)
            return ResponseEntity.badRequest().body("User not found");
        Optional<Post> post = postService.findById(requestDTO.getPostId());
        if (post.isEmpty())
            return ResponseEntity.badRequest().body("Post not found");

        GroupProfileResponse postGroup = null;
        UserProfileResponse userProfileResponse = userClientService.getUser(userId);

        Share share = new Share();
        share.setContent(requestDTO.getContent());
        share.setCreateAt(requestDTO.getCreateAt());
        share.setUpdateAt(requestDTO.getCreateAt());
        share.setPost(post.get());
        share.setUserId(user.getUserId());
        share.setPrivacyLevel(requestDTO.getPrivacyLevel());
        if (requestDTO.getPostGroupId() != null)
            if (requestDTO.getPostGroupId() != "" || requestDTO.getPostGroupId() != null) {
                postGroup = groupClientService.getGroup(requestDTO.getPostGroupId());
            if (postGroup != null) {
                share.setPostGroupId(postGroup.getId());
            }

        }
        save(share);
        SharesResponse sharesResponse = new SharesResponse(share, userProfileResponse, postGroup);
        List<Integer> count = new ArrayList<>();
        sharesResponse.setComments(count);
        sharesResponse.setLikes(count);

        GenericResponse response = GenericResponse.builder().success(true).message("Share Post Successfully")
                .result(sharesResponse).statusCode(200).build();

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Object> updateSharePost(SharePostRequestDTO requestDTO, String currentUserId) {
        Optional<Share> shareOp = findById(requestDTO.getShareId());
        if (shareOp.isEmpty())
            return ResponseEntity.badRequest().body("Share post doesn't exist");
        if (!currentUserId.equals(shareOp.get().getUserId()))
            return ResponseEntity.badRequest().body("Update denied");
        Share share = shareOp.get();
        share.setContent(requestDTO.getContent());
        share.setPrivacyLevel(requestDTO.getPrivacyLevel());
        if (requestDTO.getPostGroupId() != null) {
            if (requestDTO.getPostGroupId() != "") {
                GroupProfileResponse groupProfileResponse = groupClientService.getGroup(requestDTO.getPostGroupId());
                if (groupProfileResponse != null)
                    share.setPostGroupId(groupProfileResponse.getId());
            }
        }

        share.setUpdateAt(requestDTO.getUpdateAt());
        save(share);
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Update successful").result(null)
                .statusCode(200).build());
    }

    @Override
    @Transactional
    public ResponseEntity<GenericResponse> deleteSharePost(String shareId, String token, String userId) {
        String jwt = token.substring(7);
        String currentUserId = jwtService.extractUserId(jwt);
        if (!currentUserId.equals(userId.replaceAll("^\"|\"$", ""))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse(false, "Delete denied!", null, HttpStatus.NOT_FOUND.value()));
        }

        Optional<Share> optionalShare = findById(shareId);

        if (optionalShare.isPresent()) {
            Share share = optionalShare.get();

            shareRepository.delete(share);

            return ResponseEntity.ok()
                    .body(new GenericResponse(true, "Delete Successful!", null, HttpStatus.OK.value()));
        }
        // Khi không tìm thấy bài post với id
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse(false, "Cannot found share post!", null, HttpStatus.NOT_FOUND.value()));
        }
    }

}
