package vn.iostar.userservice.dto.response;

import lombok.Data;
import vn.iostar.userservice.entity.User;

@Data
public class UserOfPostResponse {
    private String userId;
    private String userName;
    private String avatarUser;

    public UserOfPostResponse(User user) {
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.avatarUser = user.getProfile().getAvatar();
    }
}
