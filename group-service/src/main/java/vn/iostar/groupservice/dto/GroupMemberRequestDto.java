package vn.iostar.groupservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GroupMemberRequestDto {

    private String id;
    @JsonProperty("user")
    private SimpleUserDto userDto;
    private String state;
    @JsonProperty("invitation")
    private GroupMemberInvitationDto invitationDto;
    private String createdAt;
    private String updatedAt;

}
