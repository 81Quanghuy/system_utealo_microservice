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

    @Builder.Default
    private String phone = "";

    private Gender gender;

    private Date dayOfBirth;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Boolean isOnline = false;

    @Builder.Default
    private boolean isVerified = false;
}
