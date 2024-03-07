package vn.iostar.groupservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import vn.iostar.groupservice.dto.SimpleUserDto;

@Data
@Builder
public class GroupMemberResponse {

    private String id;
    @JsonProperty("user")
    private SimpleUserDto userDto;
    private Boolean isLocked;
    private String lockedAt;
    private String lockedReason;
    private String role;
    private String createdAt;
    private String updatedAt;

}
