package vn.iostar.userservice.dto.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;
import vn.iostar.userservice.entity.User;
import vn.iostar.userservice.constant.Gender;
import vn.iostar.userservice.constant.RoleName;

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
	private boolean isAccountActive;
	private Date createdAt;
	private Date updatedAt;
	private RoleName roleName;
//	private List<FriendResponse> friends = new ArrayList<>();
//
//	private List<GroupPostResponse> postGroup;

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
        if(user.getIsActive()==true) {
            this.isActive = "Hoạt động";
        } else if (user.getIsActive()==false) {
            this.isActive = "Bị khóa";
        }
        this.isAccountActive = user.getAccount().getIsActive();
        this.createdAt = user.getAccount().getCreatedAt();
        this.updatedAt = user.getAccount().getUpdatedAt();
        this.roleName = user.getRole().getRoleName();
	}

}
