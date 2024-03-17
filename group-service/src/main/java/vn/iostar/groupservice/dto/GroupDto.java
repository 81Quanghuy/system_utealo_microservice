package vn.iostar.groupservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import vn.iostar.groupservice.dto.response.UserProfileResponse;

import java.io.Serializable;
import java.util.Set;

@Data
@Builder
public class GroupDto implements Serializable {

    private String id;
    private String postGroupName;
    private String bio;
    @JsonProperty("author")
    private UserProfileResponse userDto;
    private String avatarUrl;
    private String coverUrl;
    private Boolean isSystem;
    private Boolean isPublic;
    private Boolean isApprovalRequired;
    private String createdAt;
    private String updatedAt;
    private Boolean isActive;

}
