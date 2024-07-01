package vn.iostar.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.iostar.constant.GroupMemberRoleType;
import vn.iostar.constant.RoleName;
import vn.iostar.constant.RoleUserGroup;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupResponse {
    private String name ;
    private String description ;
    private String id ;
    private String userId ;
    private String createdAt ;
    private String updatedAt ;
    private String username;
    private GroupMemberRoleType role;
    private RoleName roleUser;
}
