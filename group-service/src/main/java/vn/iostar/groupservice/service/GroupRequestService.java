package vn.iostar.groupservice.service;

import vn.iostar.groupservice.dto.PostGroupDTO;
import vn.iostar.groupservice.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface GroupRequestService {
    ResponseEntity<GenericResponse> acceptPostGroup(String postGroupId, String currentUserId) ;

    ResponseEntity<GenericResponse> getPostGroupInvitedByUserId(String currentUserId);

    ResponseEntity<GenericResponse> getPostGroupRequestsSentByUserId(String currentUserId);

    ResponseEntity<GenericResponse> cancelRequestPostGroup(String postGroupId, String currentUserId);

    ResponseEntity<GenericResponse> declinePostGroup(String postGroupId, String currentUserId);

    ResponseEntity<GenericResponse> invitePostGroup(PostGroupDTO postGroup, String currentUserId);

    ResponseEntity<GenericResponse> joinPostGroup(String postGroupId, String currentUserId);

    ResponseEntity<GenericResponse> declineMemberRequiredByPostId(PostGroupDTO postGroup, String currentUserId);
}
