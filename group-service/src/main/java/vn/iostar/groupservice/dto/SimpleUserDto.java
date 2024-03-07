package vn.iostar.groupservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimpleUserDto {

    private String id;

    private String firstName;

    private String lastName;

    private String avatarUrl;

    private String role;

    private String gender;

    private String email;

    private String dob;
}
