package vn.iostar.userservice.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import vn.iostar.userservice.constant.Gender;

import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
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
