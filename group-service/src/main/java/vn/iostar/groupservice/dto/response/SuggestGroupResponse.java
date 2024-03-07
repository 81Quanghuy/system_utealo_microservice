package vn.iostar.groupservice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import vn.iostar.groupservice.dto.GroupDto;

import java.io.Serializable;

@Data
@Builder
public class SuggestGroupResponse implements Serializable {

    @JsonProperty("group")
    private GroupDto groupDto;
    public Integer memberCount;
    private Boolean isMember;

}
