package vn.iostar.groupservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EventDto {

    private String id;
    @JsonProperty("author")
    private SimpleUserDto userDto;
    @JsonProperty("group")
    private GroupDto groupDto;
    private String name;
    private String description;
    private String startedAt;
    private String endedAt;
    private String createdAt;
    private String updatedAt;

}
