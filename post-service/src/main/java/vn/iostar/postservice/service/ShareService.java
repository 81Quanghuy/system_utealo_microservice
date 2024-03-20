package vn.iostar.postservice.service;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.SharePostRequestDTO;
import vn.iostar.postservice.dto.response.SharesResponse;
import vn.iostar.postservice.entity.Share;

import java.util.List;
import java.util.Optional;

public interface ShareService {
    <S extends Share> S save(S entity);
    Optional<Share> findById(String id);
    ResponseEntity<Object> sharePost(String token, SharePostRequestDTO requestDTO);
    ResponseEntity<Object> updateSharePost(SharePostRequestDTO requestDTO, String currentUserId);
    ResponseEntity<GenericResponse> deleteSharePost(String shareId, String token, String userId);
    List<SharesResponse> findUserSharePosts(String currentUserId, String userId, Pageable pageable);
    SharesResponse getSharePost(Share share, String currentUserId);
}
