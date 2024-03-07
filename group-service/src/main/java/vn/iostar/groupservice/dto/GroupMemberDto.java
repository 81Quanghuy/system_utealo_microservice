package vn.iostar.groupservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GroupMemberDto {

    private String id;
    @JsonProperty("user")
    private SimpleUserDto userDto;
    private Boolean isLocked;
    private String lockedAt;
    private String lockedReason;
    @JsonIgnore
    @JsonProperty("group")
    private GroupDto groupDto;
    private String role;
    @JsonProperty("from_request")
    private GroupMemberRequestDto groupMemberRequestDto;
    private String createdAt;
    private String updatedAt;

}
