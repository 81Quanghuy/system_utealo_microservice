package vn.iostar.emailservice.dto;

import lombok.*;
import vn.iostar.constant.Gender;

import java.util.Date;

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
