package vn.iostar.groupservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GroupMemberInvitationDto {

    private String id;
    @JsonProperty("inviter")
    private SimpleUserDto inviterDto;
    @JsonProperty("receiver")
    private SimpleUserDto receiverDto;
    @JsonProperty("group")
    private SimpleGroupDto groupDto;
    private String state;
    private String createdAt;
    private String updatedAt;

}
