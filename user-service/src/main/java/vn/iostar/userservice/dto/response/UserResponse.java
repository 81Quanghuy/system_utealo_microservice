package vn.iostar.userservice.dto.response;

import lombok.Data;
import vn.iostar.userservice.constant.Gender;
import vn.iostar.userservice.constant.RoleName;

import java.util.Date;

@Data
public class UserResponse {

	private String userId;
	private String userName;
	private String address;
	private String phone;
	private Gender gender;
	private Date dayOfBirth;
	private String isActive;
	private RoleName roleName;
	private String email;
	private Boolean status;
	private Long countPost;
	private Long countShare;
	private Long countComment;

	public UserResponse(String userId, String userName, String address, String phone, Gender gender, Date dayOfBirth) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.address = address;
		this.phone = phone;
		this.gender = gender;
		this.dayOfBirth = dayOfBirth;
	}
	
	public UserResponse(String userId, String userName, String address, String phone, Gender gender, Date dayOfBirth, Boolean status ) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.address = address;
		this.phone = phone;
		this.gender = gender;
		this.dayOfBirth = dayOfBirth;
		this.status = status;
	}

}
