package vn.iostar.userservice.dto.response;

import lombok.Data;
import vn.iostar.constant.Gender;
import vn.iostar.constant.RoleName;
import vn.iostar.userservice.dto.UserFileDTO;
import vn.iostar.userservice.entity.User;

import java.util.Date;

@Data
public class UserResponse {

	private String userId;
	private String userName;
	private String address;
	private String phone;
	private Gender gender;
	private Date dayOfBirth;
	private Boolean isActive;
	private UserFileDTO userFile;
	private RoleName roleName;
	private String email;
	private Boolean status;
	private Boolean isOnline;
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
	public UserResponse(User user) {
		super();
		this.userId = user.getUserId();
		this.userName = user.getUserName();
		this.address = user.getAddress();
		this.phone = user.getPhone();
		this.gender = user.getGender();
		this.dayOfBirth = user.getDayOfBirth();
		this.isActive = user.getIsActive();
		this.roleName = user.getRole().getRoleName();
		this.email = user.getAccount().getEmail();
		this.isOnline = user.getIsOnline();
	}
}
