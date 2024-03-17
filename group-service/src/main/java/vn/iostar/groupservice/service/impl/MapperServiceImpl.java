package vn.iostar.groupservice.service.impl;

import vn.iostar.groupservice.constant.AppConstant;
import vn.iostar.groupservice.dto.*;
import vn.iostar.groupservice.dto.response.GroupMemberResponse;
import vn.iostar.groupservice.dto.response.PostGroupResponse;
import vn.iostar.groupservice.dto.response.UserProfileResponse;
import vn.iostar.groupservice.entity.*;
import vn.iostar.groupservice.service.MapperService;
import vn.iostar.groupservice.service.client.UserClientService;
import vn.iostar.groupservice.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class MapperServiceImpl implements MapperService {
    private final  UserClientService userClientService;

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
    public GroupMemberDto mapToGroupMemberDto(GroupMember groupMember) {
        return GroupMemberDto.builder()
                .id(groupMember.getId())
                .userDto(this.mapToSimpleUserDto(groupMember.getUserId()))
                .isLocked(groupMember.getIsLocked())
                .lockedAt(groupMember.getLockedAt() == null ?
                        null : DateUtil.date2String(groupMember.getLockedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .lockedReason(groupMember.getLockedReason() == null ?
                        null : groupMember.getLockedReason())
                .groupDto(this.mapToGroupDto(groupMember.getGroup()))
                .role(groupMember.getRole().name())
                .groupMemberRequest(groupMember.getMemberRequestId() == null ?
                        null : groupMember.getMemberRequestId())
                .createdAt(groupMember.getCreatedAt() == null ?
                        null : DateUtil.date2String(groupMember.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(groupMember.getUpdatedAt() == null ?
                        null : DateUtil.date2String(groupMember.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }

    @Override
    public UserDto mapToUserDto(String userId) {
        return null;
    }

    @Override
    public GroupMemberRequestDto mapToGroupMemberRequestDto(GroupRequest groupMemberRequest) {
        return null;
    }


    @Override
    public EventDto mapToEventDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .groupDto(this.mapToGroupDto(event.getGroup()))
                .userDto(this.mapToSimpleUserDto(event.getAuthorId()))
                .name(event.getTitle())
                .description(event.getDescription() == null ?
                        null : event.getDescription())
                .startedAt(event.getStartedAt() == null ?
                        null : DateUtil.date2String(event.getStartedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .endedAt(event.getEndedAt() == null ?
                        null : DateUtil.date2String(event.getEndedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .createdAt(event.getCreatedAt() == null ?
                        null : DateUtil.date2String(event.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(event.getUpdatedAt() == null ?
                        null : DateUtil.date2String(event.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }

    @Override
    public UserProfileResponse mapToSimpleUserDto(String userId) {
       return userClientService.getProfileByUserId(userId);
    }

    @Override
    public GroupMemberResponse mapToGroupMemberResponse(GroupMember groupMember) {
        return GroupMemberResponse.builder()
                .id(groupMember.getId())
                .userDto(this.mapToSimpleUserDto(groupMember.getUserId()))
                .isLocked(groupMember.getIsLocked())
                .lockedAt(groupMember.getLockedAt() == null ?
                        null : DateUtil.date2String(groupMember.getLockedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .lockedReason(groupMember.getLockedReason() == null ?
                        null : groupMember.getLockedReason())
                .role(groupMember.getRole().name())
                .createdAt(groupMember.getCreatedAt() == null ?
                        null : DateUtil.date2String(groupMember.getCreatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .updatedAt(groupMember.getUpdatedAt() == null ?
                        null : DateUtil.date2String(groupMember.getUpdatedAt(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .build();
    }

    @Override
    public SimpleGroupDto mapToSimpleGroupDto(Group group) {
        return SimpleGroupDto.builder()
                .id(group.getId())
                .name(group.getPostGroupName())
                .description(group.getBio() == null ?
                        null : group.getBio())
                .avatarUrl(group.getAvatarGroup())
                .coverUrl(group.getBackgroundGroup())
                .isPublic(group.getIsPublic())
                .build();
    }

}
