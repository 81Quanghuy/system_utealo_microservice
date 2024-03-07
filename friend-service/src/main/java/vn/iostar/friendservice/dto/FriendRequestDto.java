package vn.iostar.friendservice.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class FriendRequestDto implements Serializable {

    private String id;
    private String senderId;
    private String recipientId;
    private String status;
    private String createdAt;
    private String updatedAt;

}
