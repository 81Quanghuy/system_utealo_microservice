package vn.iostar.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.iostar.userservice.entity.User;


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

	public FriendResponse(User user) {
		this.userId = user.getUserId();
		this.background = user.getProfile().getBackground();
		this.avatar = user.getProfile().getAvatar();
		this.username = user.getUserName();
		this.isOnline = user.getIsOnline();
	}
}
