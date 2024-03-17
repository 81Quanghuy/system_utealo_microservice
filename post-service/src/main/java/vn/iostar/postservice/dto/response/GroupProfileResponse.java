package vn.iostar.postservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupProfileResponse {
    private String id;
    private String groupName;
    private String groupAvatar;
    private String groupType;
}
