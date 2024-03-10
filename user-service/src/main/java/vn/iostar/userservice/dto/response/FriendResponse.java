package vn.iostar.userservice.dto.response;

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

//	public FriendResponse(String userId, Friend friend) {
//		super();
//		if (userId.equals(friend.getUser1().getUserId())) {
//			this.userId = friend.getUser1().getUserId();
//			this.background = friend.getUser1().getProfile().getBackground();
//			this.avatar = friend.getUser1().getProfile().getAvatar();
//			this.username = friend.getUser1().getUserName();
//			this.isOnline = friend.getUser1().getIsOnline();
//		} else if (userId.equals(friend.getUser2().getUserId())) {
//			this.userId = friend.getUser2().getUserId();
//			this.background = friend.getUser2().getProfile().getBackground();
//			this.avatar = friend.getUser2().getProfile().getAvatar();
//			this.username = friend.getUser2().getUserName();
//			this.isOnline = friend.getUser2().getIsOnline();
//		}
//	}
}
