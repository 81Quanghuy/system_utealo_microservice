
package vn.iostar.friendservice.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class FriendRequestDto implements Serializable {

    private String friendRequestId;
    private String userFromId;
    private String userToId;
    private boolean isActive;
    private Date createdAt;
    private Date updatedAt;
}
