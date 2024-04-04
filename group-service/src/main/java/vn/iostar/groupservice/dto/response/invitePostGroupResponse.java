package vn.iostar.groupservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.iostar.groupservice.dto.UserInviteGroup;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class invitePostGroupResponse {
    private String message;
    private List<UserInviteGroup> userInviteGroups;
}
