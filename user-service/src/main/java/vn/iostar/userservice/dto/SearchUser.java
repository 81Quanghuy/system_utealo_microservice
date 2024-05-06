package vn.iostar.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchUser {
	
	private String userId;
	private String userName;
	private String bio;
	private String avatar;
	private String background;
	private String checkStatusFriend;
	private String address;
	private int numberFriend;
	
	public SearchUser(String userId, String userName) {
		super();
		this.userId = userId;
		this.userName = userName;
	}
	
	
}
