package vn.iotstart.userservice.dto;

import lombok.Builder;
import lombok.Data;
import vn.iotstart.userservice.constant.Gender;
import vn.iotstart.userservice.constant.RoleName;

@Data
@Builder
public class SimpleUserDto {

    private String id;
    private String firstName;
    private String lastName;
    private RoleName role;
    private Gender gender;
    private String email;
    private String dob;
}
