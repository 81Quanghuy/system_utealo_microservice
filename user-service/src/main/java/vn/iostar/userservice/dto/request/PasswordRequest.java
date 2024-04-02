package vn.iostar.userservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordRequest {
    private String email;
    private String otp;

}
