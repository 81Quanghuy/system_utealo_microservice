package vn.iostar.groupservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.iostar.groupservice.constant.AppConstant;
import vn.iostar.groupservice.constant.GroupMemberRoleType;
import vn.iostar.groupservice.dto.GroupDto;
import vn.iostar.groupservice.dto.SearchPostGroup;
import vn.iostar.groupservice.dto.response.GroupPostResponse;
import vn.iostar.groupservice.dto.response.GroupProfileResponse;
import vn.iostar.groupservice.dto.response.PostGroupResponse;
import vn.iostar.groupservice.dto.response.UserProfileResponse;
import vn.iostar.groupservice.entity.Group;
import vn.iostar.groupservice.entity.GroupMember;
import vn.iostar.groupservice.repository.GroupMemberRepository;
import vn.iostar.groupservice.service.GroupMemberService;
import vn.iostar.groupservice.service.MapperService;
import vn.iostar.groupservice.service.client.UserClientService;
import vn.iostar.groupservice.util.DateUtil;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MapperServiceImpl implements MapperService {
    private final  UserClientService userClientService;
    private final GroupMemberRepository groupMemberRepository;

    @Override
    public GroupDto mapToGroupDto(Group group) {
        return GroupDto.builder()
                .id(group.getId())
                .postGroupName(group.getPostGroupName())
                .bio(group.getBio() == null ?
                        null : group.getBio())
                .isPublic(group.getIsPublic() != null ? group.getIsPublic() : true)
                .isApprovalRequired(group.getIsApprovalRequired() != null ? group.getIsApprovalRequired() : true)
                .userDto(this.mapToSimpleUserDto(group.getAuthorId()))
                .avatarUrl(group.getAvatarGroup())
                .isActive(group.getIsActive() != null ? group.getIsActive() : true)
                .isSystem(group.getIsSystem() != null ? group.getIsSystem() : false)
                .coverUrl(group.getBackgroundGroup())
                .createdAt(group.getCreatedAt() == null ?
                        null : DateUtil.date2String(group.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(group.getUpdatedAt() == null ?
                        null : DateUtil.date2String(group.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }

    @Override
    public PostGroupResponse mapToPostGroupResponse(Group group, Integer countMember, List<String> managerId) {
        return PostGroupResponse.builder()
                .postGroupId(group.getId())
                .postGroupName(group.getPostGroupName())
                .bio(group.getBio() == null ?
                        null : group.getBio())
                .groupType(group.getIsPublic()? "Public" : "Private")
                .userJoinStatus(group.getIsApprovalRequired() ? "denied" : "allowed")
                .avatar(group.getAvatarGroup())
                .background(group.getBackgroundGroup())
                .countMember(countMember)
                .isActive(group.getIsActive() != null ? group.getIsActive() : true)
                .managerId(managerId)
                .build();
    }
    @Override
    public UserProfileResponse mapToSimpleUserDto(String userId) {
       return userClientService.getProfileByUserId(userId);
    }




    @Override
    public GroupProfileResponse mapToGroupProfileResponse(Group group) {
        return GroupProfileResponse.builder()
                .id(group.getId())
                .groupName(group.getPostGroupName())
                .groupAvatar(group.getAvatarGroup())
                .groupType(group.getIsPublic() ? "public" : "private")
                .build();
    }

    @Override
    public GroupPostResponse mapToGroupPostResponse(GroupMember groupMember) {
        return GroupPostResponse.builder()
                .id(groupMember.getGroup().getId())
                .postGroupName(groupMember.getGroup().getPostGroupName())
                .avatarGroup(groupMember.getGroup().getAvatarGroup())
                .backgroundGroup(groupMember.getGroup().getBackgroundGroup())
                .role(groupMember.getRole())
                .build();
    }

    @Override
    public SearchPostGroup mapToSearchPostGroup(Group group) {
        return SearchPostGroup.builder()
                .id(group.getId())
                .postGroupName(group.getPostGroupName())
                .avatarGroup(group.getAvatarGroup())
                .bio(group.getBio())
                .isPublic(group.getIsPublic())
                .build();
    }

}
