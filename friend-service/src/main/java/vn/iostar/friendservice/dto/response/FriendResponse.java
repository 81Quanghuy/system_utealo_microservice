package vn.iostar.friendservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendResponse {
    private String userId;
    private String background;
    private String avatar;
    private String username;
    private Boolean isOnline;
}
