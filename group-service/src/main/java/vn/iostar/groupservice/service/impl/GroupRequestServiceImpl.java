package vn.iostar.groupservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.iostar.groupservice.constant.GroupMemberRoleType;
import vn.iostar.groupservice.dto.PostGroupDTO;
import vn.iostar.groupservice.dto.response.GenericResponse;
import vn.iostar.groupservice.dto.response.InvitedPostGroupResponse;
import vn.iostar.groupservice.dto.response.UserProfileResponse;
import vn.iostar.groupservice.entity.Group;
import vn.iostar.groupservice.entity.GroupMember;
import vn.iostar.groupservice.entity.GroupRequest;
import vn.iostar.groupservice.exception.wrapper.NotFoundException;
import vn.iostar.groupservice.repository.GroupMemberRepository;
import vn.iostar.groupservice.repository.GroupRepository;
import vn.iostar.groupservice.repository.GroupRequestRepository;
import vn.iostar.groupservice.service.GroupRequestService;
import vn.iostar.groupservice.service.client.UserClientService;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupRequestServiceImpl implements GroupRequestService {
    private final GroupRepository groupRepository;
    private final GroupRequestRepository groupRequestRepository;
    private final UserClientService userClientService;
    private final GroupMemberRepository groupMemberRepository;

    @Override
    public ResponseEntity<GenericResponse> acceptPostGroup(String postGroupId, String currentUserId) {
        log.info("GroupMemberRequestServiceImpl, acceptPostGroup");
        Optional<GroupRequest> optionalGroupRequest = groupRequestRepository.findByGroupIdAndInvitedUserAndIsAccept(postGroupId, currentUserId,false);
        if (optionalGroupRequest.isEmpty()) {
            throw new NotFoundException("Group request not found");
        }

        if(optionalGroupRequest.get().getGroup().getIsApprovalRequired() &&  !CheckGroupRole(optionalGroupRequest.get().getInvitingUser(), postGroupId) ){
            optionalGroupRequest.get().setIsAccept(true);
            groupRequestRepository.save(optionalGroupRequest.get());
        }
        GroupMember groupMember = GroupMember.builder()
                .id(UUID.randomUUID().toString())
                .group(optionalGroupRequest.get().getGroup())
                .role(GroupMemberRoleType.Member)
                .userId(currentUserId)
                .memberRequestId(optionalGroupRequest.get().getInvitingUser())
                .createdAt(new Date())
                .build();
        groupMemberRepository.save(groupMember);

        groupRequestRepository.delete(optionalGroupRequest.get());
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Accept successfully")
                .statusCode(HttpStatus.OK.value()).build());
    }

    @Override
    public ResponseEntity<GenericResponse> getPostGroupInvitedByUserId(String currentUserId) {
        log.info("GroupMemberRequestServiceImpl, getPostGroupInvitedByUserId");
        List<GroupRequest> list = groupRequestRepository.findAllByInvitedUser(currentUserId);
        // chuyển đổi từ entity sang dto
        List<InvitedPostGroupResponse> invitedPostGroupResponses = list.stream().map(groupRequest -> {
            if(groupRequest.getInvitedUser().equals(groupRequest.getInvitingUser())){
                throw new NotFoundException("Group request not found");
            }
            Optional<Group> group = groupRepository.findById(groupRequest.getGroup().getId());
            if (group.isEmpty()) {
                throw new NotFoundException("Group not found");
            }
            UserProfileResponse userProfileResponse = userClientService.getProfileByUserId(groupRequest.getInvitingUser());
            return InvitedPostGroupResponse.builder()
                    .postGroupRequestId(groupRequest.getId())
                    .postGroupId(groupRequest.getGroup().getId())
                    .postGroupName(group.get().getPostGroupName())
                    .avatarGroup(group.get().getAvatarGroup())
                    .bio(group.get().getBio())
                    .backgroundGroup(group.get().getBackgroundGroup())
                    .userName(userProfileResponse.getUserName())
                    .avatarUser1(userProfileResponse.getAvatar())
                    .userId(groupRequest.getInvitingUser())
                    .build();
        }).toList();
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get successfully").result(invitedPostGroupResponses)
                .statusCode(HttpStatus.OK.value()).build());
    }

    @Override
    public ResponseEntity<GenericResponse> getPostGroupRequestsSentByUserId(String currentUserId) {
        log.info("GroupMemberRequestServiceImpl, getPostGroupRequestsSentByUserId");

        List<GroupRequest> list = groupRequestRepository.findAllByInvitingUser(currentUserId);
        // chuyển đổi từ entity sang dto
        List<InvitedPostGroupResponse> invitedPostGroupResponses = list.stream().map(groupRequest -> {
            Optional<Group> group = groupRepository.findById(groupRequest.getGroup().getId());
            if (group.isEmpty()) {
                throw new NotFoundException("Group not found");
            }
            UserProfileResponse userProfileResponse = userClientService.getProfileByUserId(groupRequest.getInvitedUser());
            return InvitedPostGroupResponse.builder()
                    .postGroupRequestId(groupRequest.getId())
                    .postGroupId(groupRequest.getGroup().getId())
                    .postGroupName(group.get().getPostGroupName())
                    .avatarGroup(group.get().getAvatarGroup())
                    .bio(group.get().getBio())
                    .backgroundGroup(group.get().getBackgroundGroup())
                    .userName(userProfileResponse.getUserName())
                    .avatarUser1(userProfileResponse.getAvatar())
                    .userId(groupRequest.getInvitedUser())
                    .build();
        }).toList();

        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get successfully").result(invitedPostGroupResponses)
                .statusCode(HttpStatus.OK.value()).build());
    }

    @Override
    public ResponseEntity<GenericResponse> cancelRequestPostGroup(String postGroupId, String currentUserId) {
        log.info("GroupMemberRequestServiceImpl, cancelRequestPostGroup");
        Optional<GroupRequest> optionalGroupRequest = groupRequestRepository.findByGroupIdAndInvitedUserAndInvitingUser(postGroupId, currentUserId,currentUserId);
        if (optionalGroupRequest.isEmpty()) {
            throw new NotFoundException("Group request not found");
        }
        groupRequestRepository.delete(optionalGroupRequest.get());
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Cancel successfully")
                .statusCode(HttpStatus.OK.value()).build());
    }

    @Override
    public ResponseEntity<GenericResponse> declinePostGroup(String postGroupId, String currentUserId) {
        log.info("GroupMemberRequestServiceImpl, declinePostGroup");
        Optional<GroupRequest> optionalGroupRequest = groupRequestRepository.findByGroupIdAndInvitedUserAndIsAccept(postGroupId, currentUserId,false);
        if (optionalGroupRequest.isEmpty()) {
            throw new NotFoundException("Group request not found");
        }
        groupRequestRepository.delete(optionalGroupRequest.get());
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Decline successfully")
                .statusCode(HttpStatus.OK.value()).build());
    }

    @Override
    public ResponseEntity<GenericResponse> invitePostGroup(PostGroupDTO postGroup, String currentUserId) {
        log.info("GroupMemberRequestServiceImpl, invitePostGroup");
        Optional<Group> group = groupRepository.findById(postGroup.getPostGroupId());
        if (group.isEmpty()) {
            throw new NotFoundException("Group not found");
        }
        for (String userId : postGroup.getUserId()) {
            Optional<GroupRequest> optionalGroupRequest = groupRequestRepository.findByGroupIdAndInvitedUserAndInvitingUser(postGroup.getPostGroupId(), userId, currentUserId);
            if (optionalGroupRequest.isPresent()) {
                throw new NotFoundException("Đã gửi lời mời vào nhóm trước đó!");
            }
            GroupRequest groupRequest = GroupRequest.builder()
                    .id(UUID.randomUUID().toString())
                    .group(group.get())
                    .invitedUser(userId)
                    .invitingUser(currentUserId)
                    .createdAt(new Date())
                    .isAccept(false)
                    .build();
            groupRequestRepository.save(groupRequest);
        }
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Invite successfully")
                .statusCode(HttpStatus.OK.value()).build());
    }

    @Override
    public ResponseEntity<GenericResponse> joinPostGroup(String postGroupId, String currentUserId) {
        log.info("GroupMemberRequestServiceImpl, joinPostGroup");
        Optional<Group> group = groupRepository.findById(postGroupId);
        if (group.isEmpty()) {
            throw new NotFoundException("Group not found");
        }
        Optional<GroupRequest> optionalGroupRequest = groupRequestRepository.findByGroupIdAndInvitedUserAndInvitingUser(postGroupId, currentUserId,currentUserId);
        if (optionalGroupRequest.isPresent()) {
            throw new NotFoundException("Đã gửi yêu cầu vào nhóm trước đó!");
        }
        if(group.get().getIsApprovalRequired()){
            GroupRequest groupRequest = GroupRequest.builder()
                    .id(UUID.randomUUID().toString())
                    .group(group.get())
                    .invitedUser(currentUserId)
                    .invitingUser(currentUserId)
                    .createdAt(new Date())
                    .isAccept(true)
                    .build();
            groupRequestRepository.save(groupRequest);
            return ResponseEntity.ok(GenericResponse.builder().success(true).message("Join successfully")
                    .result("Waiting Accept").statusCode(HttpStatus.OK.value()).build());
        }else{
            GroupMember groupMember = GroupMember.builder()
                    .id(UUID.randomUUID().toString())
                    .group(group.get())
                    .role(GroupMemberRoleType.Member)
                    .userId(currentUserId)
                    .memberRequestId(currentUserId)
                    .createdAt(new Date())
                    .build();
            groupMemberRepository.save(groupMember);
            return ResponseEntity.ok(GenericResponse.builder().success(true).message("Join successfully").result("Member")
                    .statusCode(HttpStatus.OK.value()).build());
        }
    }

    @Override
    public ResponseEntity<GenericResponse> declineMemberRequiredByPostId(PostGroupDTO postGroup, String currentUserId) {
        log.info("GroupMemberRequestServiceImpl, declineMemberRequiredByPostId");
        Optional<Group> group = groupRepository.findById(postGroup.getPostGroupId());
        if (group.isEmpty()) {
            throw new NotFoundException("Group not found");
        }
        Optional<GroupMember> groupMember = groupMemberRepository.findByUserIdAndGroupId(currentUserId,postGroup.getPostGroupId());
        if (groupMember.isEmpty()) {
            throw new NotFoundException("Group member not found");
        }
        if(groupMember.get().getRole().equals(GroupMemberRoleType.Member)){
            throw new NotFoundException("You are not admin or deputy");
        }
        List<GroupRequest> list = groupRequestRepository.findAllByGroupIdAndIsAccept(postGroup.getPostGroupId(),true);
        for (GroupRequest groupRequest : list) {
           for (String userId : postGroup.getUserId()) {
               if(groupRequest.getInvitedUser().equals(userId)){
                   groupRequestRepository.delete(groupRequest);
               }
           }
        }
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Decline successfully")
                .statusCode(HttpStatus.OK.value()).build());
    }

    @Override
    public ResponseEntity<GenericResponse> cancelPostGroupInvitation(String postGroupRequestId, String currentUserId) {
        log.info("GroupMemberRequestServiceImpl, cancelPostGroupInvitation");
        Optional<GroupRequest> optionalGroupRequest = groupRequestRepository.findByIdAndIsAccept(postGroupRequestId,false);
        if (optionalGroupRequest.isEmpty() ) {
            throw new NotFoundException("Group request not found");
        }
        if (!optionalGroupRequest.get().getInvitingUser().equals(currentUserId)) {
            throw new NotFoundException("You are not the owner of the invitation");
        }
        groupRequestRepository.delete(optionalGroupRequest.get());
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Cancel successfully")
                .statusCode(HttpStatus.OK.value()).build());
    }

    private Boolean CheckGroupRole(String currentUserId, String postGroupId) {
         Optional<GroupMember> groupMember = groupMemberRepository.findByUserIdAndGroupId(currentUserId, postGroupId);
         if (groupMember.isEmpty()) {
             throw new NotFoundException("Group member not found");
         }
         return groupMember.get().getRole().equals(GroupMemberRoleType.Admin) || groupMember.get().getRole().equals(GroupMemberRoleType.Deputy);
     }
}
