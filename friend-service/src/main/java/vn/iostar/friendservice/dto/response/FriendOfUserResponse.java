package vn.iostar.friendservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendOfUserResponse {

    private String userId;
    private Boolean isFriendOfMe;

}
