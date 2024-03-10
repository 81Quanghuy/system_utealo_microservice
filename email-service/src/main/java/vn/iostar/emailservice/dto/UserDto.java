package vn.iostar.emailservice.dto;

import lombok.*;
import vn.iostar.emailservice.constant.Gender;

import java.util.Date;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String userId;
    private String userName;
    private String address;
    private String phone = "";
    private Gender gender;
    private Date dayOfBirth;
    private Boolean isActive = true;
    private Boolean isOnline = false;
    private boolean isVerified = false;
}
