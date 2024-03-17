package vn.iostar.groupservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.iostar.groupservice.constant.Gender;
import vn.iostar.groupservice.constant.RoleName;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
