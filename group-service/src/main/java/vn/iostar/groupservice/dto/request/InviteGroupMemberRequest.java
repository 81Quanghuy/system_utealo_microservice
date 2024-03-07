package vn.iostar.groupservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InviteGroupMemberRequest {

    @NotBlank(message = "groupId is required")
    private String groupId;
    @NotBlank(message = "userId is required")
    private String userId;

}
