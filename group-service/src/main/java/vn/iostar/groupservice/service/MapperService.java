package vn.iostar.groupservice.service;

import vn.iostar.groupservice.dto.*;
import vn.iostar.groupservice.dto.response.GroupMemberResponse;
import vn.iostar.groupservice.dto.response.GroupProfileResponse;
import vn.iostar.groupservice.dto.response.PostGroupResponse;
import vn.iostar.groupservice.dto.response.UserProfileResponse;
import vn.iostar.groupservice.entity.Event;
import vn.iostar.groupservice.entity.Group;
import vn.iostar.groupservice.entity.GroupMember;
import vn.iostar.groupservice.entity.GroupRequest;

import java.util.List;

public interface MapperService {
    GroupDto mapToGroupDto(Group group);
    PostGroupResponse mapToPostGroupResponse(Group group, Integer countMember, List<String> managerId);
    GroupMemberDto mapToGroupMemberDto(GroupMember groupMember);
    UserDto mapToUserDto(String userId);
    GroupMemberRequestDto mapToGroupMemberRequestDto(GroupRequest groupMemberRequest);
    EventDto mapToEventDto(Event event);
    UserProfileResponse mapToSimpleUserDto(String userId);
    GroupMemberResponse mapToGroupMemberResponse(GroupMember groupMember);
    SimpleGroupDto mapToSimpleGroupDto(Group group);
    GroupProfileResponse mapToGroupProfileResponse(Group group);
}
