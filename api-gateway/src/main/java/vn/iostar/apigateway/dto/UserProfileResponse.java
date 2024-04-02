package vn.iostar.apigateway.dto;

import lombok.Builder;
import lombok.Data;
import vn.iostar.apigateway.constant.Gender;
import vn.iostar.apigateway.constant.RoleName;

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
    private Boolean isVerified;
}
