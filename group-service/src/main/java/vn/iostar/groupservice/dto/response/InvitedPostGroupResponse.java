package vn.iostar.groupservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitedPostGroupResponse {
    private String postGroupRequestId;
    private String postGroupId;
    private String avatarGroup;
    private String backgroundGroup;
    private String bio;
    private String postGroupName;
    private String userName;
    private String avatarUser1;
    private String userId;

}
