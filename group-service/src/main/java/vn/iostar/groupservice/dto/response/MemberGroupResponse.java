package vn.iostar.groupservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.iostar.constant.GroupMemberRoleType;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberGroupResponse {
    private String userId;
    private String username;
    private String avatarUser;
    private String backgroundUser;
    private String groupName;
    private GroupMemberRoleType roleName;
    private Date createAt;

}
