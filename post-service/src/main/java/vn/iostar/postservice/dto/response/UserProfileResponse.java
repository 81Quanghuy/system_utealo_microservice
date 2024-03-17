package vn.iostar.postservice.dto.response;

import lombok.Builder;
import lombok.Data;
import vn.iostar.postservice.constant.Gender;
import vn.iostar.postservice.constant.RoleName;


import java.util.Date;

@Data
@Builder
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
//	private List<GroupPostResponse> postGroup;


}
