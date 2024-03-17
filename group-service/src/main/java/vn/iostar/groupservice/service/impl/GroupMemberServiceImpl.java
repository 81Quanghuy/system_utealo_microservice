package vn.iostar.groupservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iostar.groupservice.constant.GroupMemberRoleType;
import vn.iostar.groupservice.constant.RoleName;
import vn.iostar.groupservice.dto.PostGroupDTO;
import vn.iostar.groupservice.dto.UserIds;
import vn.iostar.groupservice.dto.request.PostGroupRequest;
import vn.iostar.groupservice.dto.response.FriendResponse;
import vn.iostar.groupservice.dto.response.GenericResponse;
import vn.iostar.groupservice.dto.response.MemberGroupResponse;
import vn.iostar.groupservice.dto.response.UserProfileResponse;
import vn.iostar.groupservice.entity.Group;
import vn.iostar.groupservice.entity.GroupMember;
import vn.iostar.groupservice.entity.GroupRequest;
import vn.iostar.groupservice.exception.wrapper.BadRequestException;
import vn.iostar.groupservice.exception.wrapper.ForbiddenException;
import vn.iostar.groupservice.exception.wrapper.NotFoundException;
import vn.iostar.groupservice.repository.GroupMemberRepository;
import vn.iostar.groupservice.repository.GroupRepository;
import vn.iostar.groupservice.repository.GroupRequestRepository;
import vn.iostar.groupservice.service.GroupMemberService;
import vn.iostar.groupservice.service.client.UserClientService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupMemberServiceImpl implements GroupMemberService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRequestRepository groupRequestRepository;
    private final UserClientService userClientService;

    @Override
    @Transactional
    public ResponseEntity<GenericResponse> acceptMemberPostGroup(PostGroupDTO postGroup, String currentUserId) {
        log.info("GroupMemberServiceImpl, acceptMemberPostGroup");
        Optional<Group> optionalGroup = groupRepository.findById(postGroup.getPostGroupId());
        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            Optional<GroupMember> optionalGroupMember = groupMemberRepository.findByUserIdAndGroupId(currentUserId, group.getId());
            if (optionalGroupMember.isPresent()) {
                GroupMember groupMember = optionalGroupMember.get();
                if (groupMember.getRole().equals(GroupMemberRoleType.Admin) || groupMember.getRole().equals(GroupMemberRoleType.Deputy)) {
                    for (String userId : postGroup.getUserId()) {
                        Optional<GroupRequest> optionalGroupRequest = groupRequestRepository.findByGroupIdAndInvitedUser(group.getId(), userId);
                        if (optionalGroupRequest.isPresent()) {
                            GroupRequest groupRequest = optionalGroupRequest.get();
                            GroupMember newGroupMember = GroupMember.builder()
                                    .group(group)
                                    .userId(userId)
                                    .memberRequestId(groupRequest.getInvitingUser())
                                    .role(GroupMemberRoleType.Member)
                                    .createdAt(new Date())
                                    .updatedAt(new Date())
                                    .build();
                            groupMemberRepository.save(newGroupMember);
                            groupRequestRepository.delete(groupRequest);
                            return ResponseEntity.ok(GenericResponse.builder()
                                    .success(true)
                                    .statusCode(200)
                                    .message("Chấp nhận thành viên vào nhóm thành công!")
                                    .result(null)
                                    .build());
                        }
                    }
                    throw new NotFoundException("Yêu cầu tham gia nhóm không tồn tại!");
                }
                throw new ForbiddenException("Bạn không có quyền chấp nhận thành viên vào nhóm!");
            }
            throw new BadRequestException("Bạn không phải là thành viên của nhóm!");
        }
        throw new NotFoundException("Nhóm không tồn tại!");
    }

    @Override
    public ResponseEntity<GenericResponse> getMemberByPostId(String postGroupId, String currentUserId) {
        log.info("GroupMemberServiceImpl, getMemberByPostId");
        Optional<Group> optionalGroup = groupRepository.findById(postGroupId);
        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            List<GroupMember> optionalGroupMember = groupMemberRepository.findAllByGroupId(group.getId());
            List<String> userIds = new ArrayList<>();
            for (GroupMember groupMember : optionalGroupMember) {
                userIds.add(groupMember.getUserId());
            }

            return ResponseEntity.ok(GenericResponse.builder()
                    .success(true)
                    .statusCode(200)
                    .message("Lấy danh sách thành viên trong nhóm thành công!")
                    .result(GetInfoListUserByListUserId(group, userIds))
                    .build());
        }
        throw new NotFoundException("Nhóm không tồn tại!");
    }

    @Override
    public ResponseEntity<GenericResponse> getMemberRequiredByPostId(String postGroupId, String currentUserId) {
        log.info("GroupMemberServiceImpl, getMemberRequiredByPostId");
        Optional<Group> optionalGroup = groupRepository.findById(postGroupId);
        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            List<GroupRequest> optionalGroupRequest = groupRequestRepository.findAllByGroupIdAndIsAccept(group.getId(), true);
            List<String> userIds = new ArrayList<>();
            for (GroupRequest groupRequest : optionalGroupRequest) {
                userIds.add(groupRequest.getInvitedUser());
            }
            return ResponseEntity.ok(GenericResponse.builder()
                    .success(true)
                    .statusCode(200)
                    .message("Lấy danh sách yêu cầu tham gia nhóm thành công!")
                    .result(GetInfoListUserByListUserId(group, userIds))
                    .build());
        }
        throw new NotFoundException("Nhóm không tồn tại!");
    }

    @Override
    public ResponseEntity<GenericResponse> assignDeputyByUserIdAndGroupId(PostGroupRequest postGroup, String currentUserId) {
        log.info("GroupMemberServiceImpl, assignDeputyByUserIdAndGroupId");
        Optional<Group> optionalGroup = groupRepository.findById(postGroup.getPostGroupId());
        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            Optional<GroupMember> optionalGroupMember = groupMemberRepository.findByUserIdAndRoleAndGroupId(currentUserId, GroupMemberRoleType.Admin, group.getId());
            if (optionalGroupMember.isPresent()) {
                Optional<GroupMember> optionalGroupMemberDeputy = groupMemberRepository.findByUserIdAndGroupId(postGroup.getUserId(), group.getId());
                if (optionalGroupMemberDeputy.isPresent()) {
                    GroupMember groupMemberDeputy = optionalGroupMemberDeputy.get();
                    groupMemberDeputy.setRole(GroupMemberRoleType.Deputy);
                    groupMemberRepository.save(groupMemberDeputy);
                    return ResponseEntity.ok(GenericResponse.builder()
                            .success(true)
                            .statusCode(200)
                            .message("Phân quyền thành công!")
                            .result(null)
                            .build());
                }
                throw new NotFoundException("Thành viên không tồn tại!");
            }
            throw new BadRequestException("Bạn không phải là quản trị viên của nhóm!");
        }
        throw new NotFoundException("Nhóm không tồn tại!");
    }

    @Override
    public ResponseEntity<GenericResponse> assignAdminByUserIdAndGroupId(PostGroupRequest postGroup, String currentUserId) {
        log.info("GroupMemberServiceImpl, assignAdminByUserIdAndGroupId");
        Optional<Group> optionalGroup = groupRepository.findById(postGroup.getPostGroupId());
        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            Optional<GroupMember> optionalGroupMember = groupMemberRepository.findByUserIdAndRoleAndGroupId(currentUserId, GroupMemberRoleType.Admin, group.getId());
            if (optionalGroupMember.isPresent()) {
                Optional<GroupMember> optionalGroupMemberAdmin = groupMemberRepository.findByUserIdAndGroupId(postGroup.getUserId(), group.getId());
                if (optionalGroupMemberAdmin.isPresent()) {
                    GroupMember groupMemberAdmin = optionalGroupMemberAdmin.get();
                    groupMemberAdmin.setRole(GroupMemberRoleType.Admin);
                    optionalGroupMember.get().setRole(GroupMemberRoleType.Member);
                    groupMemberRepository.save(optionalGroupMember.get());
                    groupMemberRepository.save(groupMemberAdmin);
                    return ResponseEntity.ok(GenericResponse.builder()
                            .success(true)
                            .statusCode(200)
                            .message("Phân quyền thành công!")
                            .result(null)
                            .build());
                }
                throw new NotFoundException("Thành viên không tồn tại!");
            }
            throw new BadRequestException("Bạn không phải là quản trị viên của nhóm!");
        }
        throw new NotFoundException("Nhóm không tồn tại!");
    }

    @Override
    public ResponseEntity<GenericResponse> removeDeputyByUserIdAndGroupId(PostGroupRequest postGroup, String currentUserId) {
        log.info("GroupMemberServiceImpl, removeDeputyByUserIdAndGroupId");
        Optional<Group> optionalGroup = groupRepository.findById(postGroup.getPostGroupId());
        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            Optional<GroupMember> optionalGroupMember = groupMemberRepository.findByUserIdAndRoleAndGroupId(currentUserId, GroupMemberRoleType.Admin, group.getId());
            if (optionalGroupMember.isPresent()) {
                Optional<GroupMember> optionalGroupMemberDeputy = groupMemberRepository.findByUserIdAndGroupId(postGroup.getUserId(), group.getId());
                if (optionalGroupMemberDeputy.isPresent()) {
                    GroupMember groupMemberDeputy = optionalGroupMemberDeputy.get();
                    groupMemberDeputy.setRole(GroupMemberRoleType.Member);
                    groupMemberRepository.save(groupMemberDeputy);
                    return ResponseEntity.ok(GenericResponse.builder()
                            .success(true)
                            .statusCode(200)
                            .message("Phân quyền thành công!")
                            .result(null)
                            .build());
                }
                throw new NotFoundException("Thành viên không tồn tại!");
            }
            throw new BadRequestException("Bạn không phải là quản trị viên của nhóm!");
        }
        throw new NotFoundException("Không tìm thấy nhóm!");
    }

    @Override
    public ResponseEntity<GenericResponse> deleteMemberByPostId(PostGroupRequest postGroup, String currentUserId) {
        log.info("GroupMemberServiceImpl, deleteMemberByPostId");
        Optional<Group> optionalGroup = groupRepository.findById(postGroup.getPostGroupId());
        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(currentUserId, group.getId())
                    .orElseThrow(() -> new BadRequestException("Bạn không phải là thành viên của nhóm!"));
            List<GroupMember> optionalGroupMember = groupMemberRepository.findAllByGroupIdAndRoleIn(group.getId(), List.of(GroupMemberRoleType.Admin, GroupMemberRoleType.Deputy));
            if (optionalGroupMember.contains(groupMember)) {
                Optional<GroupMember> optionalGroupMemberDelete = groupMemberRepository.findByUserIdAndGroupId(postGroup.getUserId(), group.getId());
                if (optionalGroupMemberDelete.isPresent() && !optionalGroupMember.contains(optionalGroupMemberDelete.get())) {
                    groupMemberRepository.delete(optionalGroupMemberDelete.get());
                    return ResponseEntity.ok(GenericResponse.builder()
                            .success(true)
                            .statusCode(200)
                            .message("Xóa thành viên khỏi nhóm thành công!")
                            .result(null)
                            .build());
                }
                throw new NotFoundException("Thành viên không tồn tại hoặc không thể xóa quản trị viên!");
            }
            throw new BadRequestException("Bạn không phải là quản trị viên của nhóm!");
        }
        throw new NotFoundException("Nhóm không tồn tại!");
    }

    @Override
    public ResponseEntity<GenericResponse> leaveGroup(String currentUserId, String postGroupId) {
        log.info("GroupMemberServiceImpl, leaveGroup");
        Optional<Group> optionalGroup = groupRepository.findById(postGroupId);
        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(currentUserId, group.getId())
                    .orElseThrow(() -> new BadRequestException("Bạn không phải là thành viên của nhóm!"));
            List<GroupMember> optionalGroupMember = groupMemberRepository.findAllByGroupIdAndRoleIn(group.getId(), List.of(GroupMemberRoleType.Admin, GroupMemberRoleType.Deputy));
            if (!optionalGroupMember.contains(groupMember)) {
                groupMemberRepository.delete(groupMember);
                return ResponseEntity.ok(GenericResponse.builder()
                        .success(true)
                        .statusCode(200)
                        .message("Rời khỏi nhóm thành công!")
                        .result(null)
                        .build());
            }
            throw new BadRequestException("Bạn không thể rời khỏi nhóm vì bạn là quản trị viên hoặc phó quản trị viên!");
        }
        throw new NotFoundException("Nhóm không tồn tại!");

    }

    @Override
    public ResponseEntity<GenericResponse> addAdminRoleInGroup(String groupId, String userId, String currentUserId) {
        log.info("GroupMemberServiceImpl, addAdminRoleInGroup");
        UserProfileResponse userProfileResponse =  userClientService.getProfileByUserId(currentUserId);
        if (userProfileResponse.getRoleName().equals(RoleName.Admin)) {
            throw new BadRequestException("Không thể thực hiện chức năng này!!!");
        }
        Optional<Group> optionalGroup = groupRepository.findById(groupId);
        if (optionalGroup.isPresent()) {
            //Chwa xong
        }
        throw new NotFoundException("Nhóm không tồn tại!");
    }

    private List<MemberGroupResponse> GetInfoListUserByListUserId(Group group, List<String> userIds) {
        List<FriendResponse> friendResponses = userClientService.getFriendByListUserId(new UserIds(userIds));
        List<MemberGroupResponse> meList = new ArrayList<>();
        for (FriendResponse member : friendResponses) {
            meList.add(MemberGroupResponse.builder()
                    .userId(member.getUserId())
                    .backgroundUser(member.getBackground())
                    .avatarUser(member.getAvatar())
                    .username(member.getUsername())
                    .groupName(group.getPostGroupName())
                    .roleName(getRoleName(member.getUserId(), group.getId()))
                    .build());
        }
        return meList;
    }

    /**
     * Get role name of user in group by userId and groupId from GroupMember
     *
     * @param userId  : userId
     * @param groupId : groupId
     * @return GroupMemberRoleType : Role name of user in group
     */
    private GroupMemberRoleType getRoleName(String userId, String groupId) {
        log.info("GroupMemberServiceImpl, getRoleName");
        Optional<GroupMember> optionalGroupMember = groupMemberRepository.findByUserIdAndGroupId(userId, groupId);
        if (optionalGroupMember.isPresent()) {
            GroupMember groupMember = optionalGroupMember.get();
            return groupMember.getRole();
        }
        return null;
    }
}
