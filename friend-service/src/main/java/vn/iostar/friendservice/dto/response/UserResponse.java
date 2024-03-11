package vn.iostar.friendservice.dto.response;

import lombok.Data;
import vn.iostar.friendservice.constant.Gender;
import vn.iostar.friendservice.constant.RoleName;

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
    private RoleName roleName;
    private String email;
    private Boolean status;
    private Boolean isOnline;
    private Long countPost;
    private Long countShare;
    private Long countComment;
}
