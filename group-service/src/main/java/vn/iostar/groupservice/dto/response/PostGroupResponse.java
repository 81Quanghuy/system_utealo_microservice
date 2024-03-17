package vn.iostar.groupservice.dto.response;

import lombok.*;

import java.util.List;
import java.util.Set;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostGroupResponse {
    private String postGroupId;
    private String postGroupName;
    private String bio;
    private Integer countMember;
    private String groupType;// true: private, false: public
    private String userJoinStatus;
    private String avatar;
    private String background;
    private String roleGroup;
    private List<String> managerId;
    private Boolean isActive;

}
