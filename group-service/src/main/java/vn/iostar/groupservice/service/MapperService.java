package vn.iostar.groupservice.service;

import vn.iostar.groupservice.dto.*;
import vn.iostar.groupservice.dto.response.GroupMemberResponse;
import vn.iostar.groupservice.dto.response.GroupProfileResponse;
import vn.iostar.groupservice.entity.*;

public interface MapperService {
    GroupDto mapToGroupDto(Group group);
    GroupMemberDto mapToGroupMemberDto(GroupMember groupMember);
    UserDto mapToUserDto(String userId);
    GroupMemberRequestDto mapToGroupMemberRequestDto(GroupRequest groupMemberRequest);
    EventDto mapToEventDto(Event event);
    SimpleUserDto mapToSimpleUserDto(String userId);
    GroupMemberResponse mapToGroupMemberResponse(GroupMember groupMember);
    SimpleGroupDto mapToSimpleGroupDto(Group group);
    GroupProfileResponse mapToGroupProfileResponse(Group group);
}
