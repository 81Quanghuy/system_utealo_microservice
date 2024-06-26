package vn.iostar.userservice.dto.response;

import java.util.Date;

import lombok.Data;
import vn.iostar.constant.Gender;
import vn.iostar.constant.RoleName;
import vn.iostar.userservice.entity.User;

@Data
public class UserProfileResponse {

	private String userId;
	private String phone;
	private String email;
	private String userName;
	private String avatar;
	private String background;
	private String address;
	private Date dayOfBirth;
	private String about;
	private Gender gender;
	private String isActive;
    private Boolean isVerified;
	private boolean isAccountActive;
    private Date lastLogin;
	private Date createdAt;
	private Date updatedAt;
	private RoleName roleName;
    private Boolean isOnline;

	public UserProfileResponse(User user) {
		this.userId = user.getUserId();
        this.phone = user.getPhone();
        this.email = user.getAccount().getEmail();
        this.userName = user.getUserName();
        this.avatar = user.getProfile().getAvatar();
        this.background = user.getProfile().getBackground();
        this.address = user.getAddress();
        this.dayOfBirth = user.getDayOfBirth();
        this.about = user.getProfile().getBio();
        this.gender = user.getGender();
        if(user.getIsActive()) {
            this.isActive = "Hoạt động";
        } else  {
            this.isActive = "Bị khóa";
        }
        this.isAccountActive = user.getAccount().getIsActive();
        this.createdAt = user.getAccount().getCreatedAt();
        this.updatedAt = user.getAccount().getUpdatedAt();
        this.lastLogin = user.getAccount().getLastLoginAt();
        this.roleName = user.getRole().getRoleName();
        this.isVerified = user.getIsVerified();
        this.isOnline = user.getIsOnline();
	}

}
