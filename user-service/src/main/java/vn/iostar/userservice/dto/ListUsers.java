package vn.iostar.userservice.dto;

import lombok.Data;

@Data
public class ListUsers {
	
	private String userId;
	private String userName;
	
	public ListUsers(String userId, String userName) {
		super();
		this.userId = userId;
		this.userName = userName;
	}
	
}
