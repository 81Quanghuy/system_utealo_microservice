package vn.iotstart.userservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RelationshipDto {

    private String id;
    @JsonProperty("parent")
    private SimpleUserDto parentDto;
    @JsonProperty("student")
    private SimpleUserDto studentDto;
    private boolean isAccepted;

}
