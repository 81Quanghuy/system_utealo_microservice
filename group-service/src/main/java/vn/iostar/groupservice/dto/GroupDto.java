package vn.iostar.groupservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class GroupDto implements Serializable {

    private String id;
    private String name;
    private String description;
    @JsonProperty("author")
    private SimpleUserDto userDto;
    private String avatarUrl;
    private String coverUrl;
    private Boolean isClass;
    private Boolean isPublic;
    private Boolean isAcceptAllRequest;
    private String subject;
    private Integer grade;
    private String createdAt;
    private String updatedAt;

}
