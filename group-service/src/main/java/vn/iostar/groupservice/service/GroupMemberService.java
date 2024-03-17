package vn.iostar.groupservice.service;

import vn.iostar.groupservice.dto.PostGroupDTO;
import vn.iostar.groupservice.dto.request.*;
import vn.iostar.groupservice.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface GroupMemberService {

    ResponseEntity<GenericResponse> acceptMemberPostGroup(PostGroupDTO postGroup, String currentUserId);

    ResponseEntity<GenericResponse> getMemberByPostId(String postGroupId, String currentUserId);

    ResponseEntity<GenericResponse> getMemberRequiredByPostId(String postGroupId, String currentUserId);

    ResponseEntity<GenericResponse> assignDeputyByUserIdAndGroupId(PostGroupRequest postGroup, String currentUserId);

    ResponseEntity<GenericResponse> assignAdminByUserIdAndGroupId(PostGroupRequest postGroup, String currentUserId);

    ResponseEntity<GenericResponse> removeDeputyByUserIdAndGroupId(PostGroupRequest postGroup, String currentUserId);

    ResponseEntity<GenericResponse> deleteMemberByPostId(PostGroupRequest postGroup, String currentUserId);

    ResponseEntity<GenericResponse> leaveGroup(String currentUserId, String postGroupId);

    ResponseEntity<GenericResponse> addAdminRoleInGroup(String groupId, String userId, String currentUserId);
}
