package vn.iostar.groupservice.service;

import vn.iostar.groupservice.dto.GroupDto;
import vn.iostar.groupservice.dto.SearchPostGroup;
import vn.iostar.groupservice.dto.response.GroupPostResponse;
import vn.iostar.groupservice.dto.response.GroupProfileResponse;
import vn.iostar.groupservice.dto.response.PostGroupResponse;
import vn.iostar.groupservice.dto.response.UserProfileResponse;
import vn.iostar.groupservice.entity.Group;
import vn.iostar.groupservice.entity.GroupMember;

import java.util.List;

public interface MapperService {
    GroupDto mapToGroupDto(Group group);
    PostGroupResponse mapToPostGroupResponse(Group group, Integer countMember, List<String> managerId);
    UserProfileResponse mapToSimpleUserDto(String userId);
    GroupProfileResponse mapToGroupProfileResponse(Group group);

    GroupPostResponse mapToGroupPostResponse(GroupMember groupMember);

    SearchPostGroup mapToSearchPostGroup(Group group);

}
