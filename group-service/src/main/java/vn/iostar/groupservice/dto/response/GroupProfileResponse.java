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
public class GroupProfileResponse implements Serializable {
    private String id;
    private String groupName;
    private String groupAvatar;
    private String groupType;
}
