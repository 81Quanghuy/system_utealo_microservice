package vn.iostar.groupservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GroupOfUserResponse implements Serializable {

    private String groupId;
    private String groupName;
    private String groupDescription;
    private String groupImage;
    private String groupType;

}
