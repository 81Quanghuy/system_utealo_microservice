package vn.iostar.friendservice.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class FriendRequestDto implements Serializable {

    private int friendRequestId;
    private String userFromId;
    private String userToId;
    private boolean isActive;

}
