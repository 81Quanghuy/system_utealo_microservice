package vn.iostar.groupservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.iostar.groupservice.constant.GroupMemberRoleType;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupPostResponse {
    private String postGroupId;
    private String postGroupName;
    private String avatarGroup;
    private String backgroundGroup;
    private GroupMemberRoleType role;
}
