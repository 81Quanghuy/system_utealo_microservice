package vn.iotstart.groupservice.dto.response;

import lombok.Builder;
import lombok.Data;
import vn.iotstart.groupservice.constant.Gender;
import vn.iotstart.groupservice.constant.RoleName;

import java.io.Serializable;
import java.util.Date;

@Builder
@Data
public class UserProfileResponse implements Serializable {
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
