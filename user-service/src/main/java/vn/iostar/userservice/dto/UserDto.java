package vn.iostar.userservice.dto;

import jakarta.persistence.*;
import lombok.*;
import vn.iostar.userservice.constant.Gender;
import vn.iostar.userservice.entity.Account;
import vn.iostar.userservice.entity.Profile;
import vn.iostar.userservice.entity.Role;
import vn.iostar.userservice.entity.Token;

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
