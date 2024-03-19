package vn.iostar.postservice.service;

import org.springframework.http.ResponseEntity;
import vn.iostar.postservice.dto.GenericResponse;
import vn.iostar.postservice.dto.SharePostRequestDTO;
import vn.iostar.postservice.entity.Share;

import java.util.Optional;

public interface ShareService {
    <S extends Share> S save(S entity);
    Optional<Share> findById(String id);
    ResponseEntity<Object> sharePost(String token, SharePostRequestDTO requestDTO);
    ResponseEntity<Object> updateSharePost(SharePostRequestDTO requestDTO, String currentUserId);
    ResponseEntity<GenericResponse> deleteSharePost(String shareId, String token, String userId);
}
