package vn.iostar.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {
	@NotBlank(message = "Current password cannot be blank")
    String password;
    @NotBlank(message = "New password cannot be blank")
    String newPassword;
    @NotBlank(message = "Confirm new password cannot be blank")
    String confirmPassword;
}
