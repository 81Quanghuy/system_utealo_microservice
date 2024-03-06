package vn.iostar.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import vn.iotstart.userservice.constant.Gender;

@Data
public class RegisterRequest {

    @NotEmpty(message = "Role name is required")
    private String roleName;

    @NotEmpty(message = "Full name is required")
    private String fullName;

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotEmpty(message = "Phone is required")
    private String phone;

    private Gender gender;

    @NotEmpty(message = "Password is required")
    private String password;

    @NotEmpty(message = "Confirm Password is required")
    private String confirmPassword;

    private String groupName;
}
