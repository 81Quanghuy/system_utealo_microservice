package vn.iostar.groupservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.iostar.constant.GroupMemberRoleType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupPostResponse {
    private String id;
    private String postGroupName;
    private String avatarGroup;
    private String backgroundGroup;
    private GroupMemberRoleType role;
}
