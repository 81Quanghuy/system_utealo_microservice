package vn.iostar.groupservice.service;

import vn.iostar.groupservice.dto.request.*;
import vn.iostar.groupservice.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface GroupMemberService {
    ResponseEntity<GenericResponse> inivteGroupMember(String userId, InviteGroupMemberRequest groupMemberRequest);

    ResponseEntity<GenericResponse> requestGroupMember(String userId, String groupId);

    ResponseEntity<GenericResponse> responseGroupMemberInvitation(String userId, String groupMemberInvitationId, StateRequest inviteResponseGroupMember);

    ResponseEntity<GenericResponse> responseGroupMemberRequest(String userId, String groupMemberRequestId, StateRequest stateRequest);

    ResponseEntity<GenericResponse> changeRole(String userId, String groupMemberId, String role);

    ResponseEntity<GenericResponse> deleteGroupMember(String userId, String groupMemberId);

    ResponseEntity<GenericResponse> addGroupMember(String userId, AddGroupMemberRequest addGroupMemberRequest);

    ResponseEntity<GenericResponse> getGroupMemberByGroupId(String userId, String groupId);

    ResponseEntity<GenericResponse> lockGroupMember(String userId, LockMemberRequest lockMemberRequest);

    ResponseEntity<GenericResponse> unlockGroupMember(String userId, UnlockMemberRequest unlockMemberRequest);

    String getGroupMemberRoleByGroupIdAndUserId(String groupId, String userId);

    ResponseEntity<GenericResponse> getAllGroupMembers(String authorizationHeader, String groupId, Integer page, Integer size);
}
